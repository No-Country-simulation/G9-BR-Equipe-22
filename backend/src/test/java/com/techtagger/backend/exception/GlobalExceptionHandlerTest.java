package com.techtagger.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * O time não tinha nenhum teste cobrindo o mapeamento exceção -> status
 * HTTP / corpo de erro. Estes testes travam o contrato hoje documentado em
 * backend/README.md ("Tratamento de erros").
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void conteudoNaoEncontradoRetorna404ComMensagem() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleNaoEncontrado(new ConteudoNaoEncontradoException(42L));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).containsEntry("error", "Not Found");
        assertThat(resposta.getBody().get("detail")).asString().contains("42");
    }

    @Test
    void mlServiceUnavailableRetorna503() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleMLUnavailable(new MLServiceUnavailableException("ML API fora do ar"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resposta.getBody()).containsEntry("error", "Service Unavailable");
        assertThat(resposta.getBody()).containsEntry("detail", "ML API fora do ar");
    }

    @Test
    void runtimeExceptionGenericaVazaMensagemInternaAoCliente() {
        /*
         * Ponto crítico documentado aqui: ao contrário de handleGeneric(),
         * handleRuntime() devolve ex.getMessage() diretamente no corpo da
         * resposta. Qualquer RuntimeException não mapeada especificamente
         * (ex.: uma NullPointerException com mensagem interna, ou uma
         * exceção lançada por uma lib de terceiros) vaza detalhes de
         * implementação para o cliente da API pública. Este teste apenas
         * comprova o comportamento atual — o ideal é que handleRuntime()
         * também devolva uma mensagem genérica, como handleGeneric() faz.
         */
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleRuntime(new RuntimeException("Connection refused: /10.0.0.5:3306"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody().get("detail")).asString().contains("10.0.0.5:3306");
    }

    @Test
    void exceptionGenericaRetorna500SemVazarMensagemInterna() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleGeneric(new RuntimeException("stack trace sensível com detalhes internos"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        // handleGeneric propositalmente não expõe ex.getMessage() ao cliente.
        assertThat(resposta.getBody().get("detail"))
                .asString()
                .doesNotContain("stack trace sensível");
    }

    @Test
    void validationExceptionRetorna400ComListaDeCampos() {
        MethodParameter parametro = mock(MethodParameter.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "conteudoRequest");
        bindingResult.addError(new FieldError("conteudoRequest", "titulo", "Título não pode ser vazio"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parametro, bindingResult);

        ResponseEntity<Map<String, Object>> resposta = handler.handleValidation(ex);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).containsEntry("error", "Validation Error");
        @SuppressWarnings("unchecked")
        List<String> detalhes = (List<String>) resposta.getBody().get("detail");
        assertThat(detalhes).contains("titulo: Título não pode ser vazio");
    }
}
