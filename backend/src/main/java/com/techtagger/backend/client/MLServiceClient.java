package com.techtagger.backend.client;

import com.techtagger.backend.dto.ml.MLRequest;
import com.techtagger.backend.dto.ml.MLResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MLServiceClient {

    private final WebClient webClient;

    public MLServiceClient(@Qualifier("mlWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public MLResponse classificar(String titulo, String texto) {
        MLRequest request = new MLRequest(titulo, texto);

        return webClient.post()
                .uri("/content")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Erro de validação no serviço de ML: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Serviço de ML indisponível: " + response.statusCode())))
                .bodyToMono(MLResponse.class)
                .block();
    }
}