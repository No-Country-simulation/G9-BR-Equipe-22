package com.techtagger.backend.client;

import com.techtagger.backend.dto.ml.MLRequest;
import com.techtagger.backend.dto.ml.MLResponse;
import com.techtagger.backend.exception.MLServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class MLServiceClient {

    private static final Logger log = LoggerFactory.getLogger(MLServiceClient.class);

    private final WebClient webClient;

    public MLServiceClient(@Qualifier("mlWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public MLResponse classificar(String titulo, String texto) {
        MLRequest request = new MLRequest(titulo, texto);
        long inicio = System.currentTimeMillis();

        try {
            MLResponse resposta = webClient.post()
                    .uri("/content")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new RuntimeException("Erro de validação no serviço de ML: " + response.statusCode())))
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new MLServiceUnavailableException("Serviço de ML indisponível: " + response.statusCode())))
                    .bodyToMono(MLResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(500))
                            .filter(this::isErroTransitorio)
                            .doBeforeRetry(sig -> log.warn("Tentando novamente chamada a ML API (tentativa {}): {}",
                                    sig.totalRetries() + 1, sig.failure().getMessage())))
                    .block();

            log.info("Classificação concluída em {}ms para título='{}'", System.currentTimeMillis() - inicio, titulo);
            return resposta;

        } catch (Exception e) {
            log.error("Falha ao chamar ML API para título='{}': {}", titulo, e.getMessage());
            throw new MLServiceUnavailableException(
                    "Não foi possível classificar o conteúdo no momento. Tente novamente em instantes.", e);
        }
    }

    private boolean isErroTransitorio(Throwable throwable) {
        return throwable instanceof WebClientRequestException
                || throwable instanceof TimeoutException
                || throwable instanceof MLServiceUnavailableException;
    }
}