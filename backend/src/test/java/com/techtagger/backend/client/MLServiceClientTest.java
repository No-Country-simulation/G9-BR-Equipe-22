package com.techtagger.backend.client;

import com.techtagger.backend.dto.ml.MLBatchResponse;
import com.techtagger.backend.dto.ml.MLRequest;
import com.techtagger.backend.dto.ml.MLResponse;
import com.techtagger.backend.exception.MLServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de MLServiceClient sem subir contexto Spring nem servidor HTTP real:
 * o WebClient é construído com um ExchangeFunction fake que devolve respostas
 * pré-programadas via WebClient.builder().exchangeFunction(...), a forma
 * recomendada pelo próprio Spring para testar consumidores de WebClient.
 * Isso permite testar o parsing de sucesso, o mapeamento de erros HTTP
 * (4xx -> RuntimeException, 5xx -> MLServiceUnavailableException, ambos
 * re-embrulhados em MLServiceUnavailableException pelo catch genérico da
 * classe) e a política de retry (Retry.backoff(2, ...) só reage a erros
 * "transitórios": 5xx, timeout ou erro de conexão -- erros de validação 4xx
 * não são repetidos, falham na primeira tentativa).
 *
 * Esta classe estava com 1% de cobertura (17 de 18 métodos sem teste) porque
 * todos os outros testes do backend mockam MLServiceClient inteiro via
 * @MockitoBean/@Mock -- nenhum exercitava a implementação real, que é o
 * ponto de integração mais frágil do sistema (chamada HTTP síncrona,
 * bloqueante, para o serviço de ML em Python).
 */
class MLServiceClientTest {

    private MLServiceClient clientComResposta(HttpStatus status, String corpoJson, AtomicInteger chamadas) {
        ExchangeFunction exchangeFunction = request -> {
            chamadas.incrementAndGet();
            return Mono.just(ClientResponse.create(status)
                    .header("Content-Type", "application/json")
                    .body(corpoJson)
                    .build());
        };
        WebClient webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();
        return new MLServiceClient(webClient);
    }

    @Test
    void classificarComRespostaOkRetornaMLResponseParseado() {
        AtomicInteger chamadas = new AtomicInteger();
        String json = """
                {
                  "category": "Backend",
                  "probability": 0.93,
                  "top_categories": [{"name": "Backend", "score": 0.93}],
                  "keywords": ["spring", "java"],
                  "related_content": [],
                  "processed_at": "2026-08-15T10:00:00"
                }
                """;
        MLServiceClient client = clientComResposta(HttpStatus.OK, json, chamadas);

        MLResponse resposta = client.classificar("Titulo", "Texto de exemplo com mais de dez caracteres.");

        assertThat(resposta.category()).isEqualTo("Backend");
        assertThat(resposta.probability()).isEqualTo(0.93);
        assertThat(resposta.keywords()).containsExactly("spring", "java");
        assertThat(chamadas.get()).isEqualTo(1);
    }

    @Test
    void classificarCom4xxNaoTentaNovamenteELancaMLServiceUnavailableException() {
        AtomicInteger chamadas = new AtomicInteger();
        MLServiceClient client = clientComResposta(HttpStatus.BAD_REQUEST, "{\"detail\": \"titulo invalido\"}", chamadas);

        assertThatThrownBy(() -> client.classificar("", "Texto"))
                .isInstanceOf(MLServiceUnavailableException.class);

        assertThat(chamadas.get()).isEqualTo(1);
    }

    @Test
    void classificarCom5xxTentaNovamenteAteEsgotarRetriesELancaMLServiceUnavailableException() {
        AtomicInteger chamadas = new AtomicInteger();
        MLServiceClient client = clientComResposta(HttpStatus.SERVICE_UNAVAILABLE, "{}", chamadas);

        assertThatThrownBy(() -> client.classificar("Titulo", "Texto de exemplo com mais de dez caracteres."))
                .isInstanceOf(MLServiceUnavailableException.class);

        // 1 tentativa inicial + 2 retries configurados em Retry.backoff(2, ...)
        assertThat(chamadas.get()).isEqualTo(3);
    }

    @Test
    void classificarLoteComRespostaOkRetornaMLBatchResponseParseado() {
        AtomicInteger chamadas = new AtomicInteger();
        String json = """
                {
                  "total": 1,
                  "results": [
                    {
                      "title": "Titulo",
                      "category": "Frontend",
                      "probability": 0.8,
                      "top_categories": [],
                      "keywords": [],
                      "related_content": []
                    }
                  ]
                }
                """;
        MLServiceClient client = clientComResposta(HttpStatus.OK, json, chamadas);

        MLBatchResponse resposta = client.classificarLote(
                List.of(new MLRequest("Titulo", "Texto de exemplo com mais de dez caracteres.")));

        assertThat(resposta.total()).isEqualTo(1);
        assertThat(resposta.results()).hasSize(1);
        assertThat(resposta.results().get(0).category()).isEqualTo("Frontend");
    }

    @Test
    void classificarLoteCom5xxTentaNovamenteELancaMLServiceUnavailableException() {
        AtomicInteger chamadas = new AtomicInteger();
        MLServiceClient client = clientComResposta(HttpStatus.INTERNAL_SERVER_ERROR, "{}", chamadas);

        assertThatThrownBy(() -> client.classificarLote(
                List.of(new MLRequest("Titulo", "Texto de exemplo com mais de dez caracteres."))))
                .isInstanceOf(MLServiceUnavailableException.class);

        assertThat(chamadas.get()).isEqualTo(3);
    }

    @Test
    void listarCategoriasComRespostaOkRetornaListaDeCategorias() {
        AtomicInteger chamadas = new AtomicInteger();
        String json = "{\"categories\": [\"Backend\", \"Frontend\", \"Data Science\"]}";
        MLServiceClient client = clientComResposta(HttpStatus.OK, json, chamadas);

        List<String> categorias = client.listarCategorias();

        assertThat(categorias).containsExactly("Backend", "Frontend", "Data Science");
        assertThat(chamadas.get()).isEqualTo(1);
    }

    @Test
    void listarCategoriasCom4xxNaoTentaNovamenteELancaMLServiceUnavailableException() {
        AtomicInteger chamadas = new AtomicInteger();
        MLServiceClient client = clientComResposta(HttpStatus.NOT_FOUND, "{}", chamadas);

        assertThatThrownBy(client::listarCategorias)
                .isInstanceOf(MLServiceUnavailableException.class);

        assertThat(chamadas.get()).isEqualTo(1);
    }
}
