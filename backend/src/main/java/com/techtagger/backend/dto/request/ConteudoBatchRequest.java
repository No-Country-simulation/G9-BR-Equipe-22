package com.techtagger.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConteudoBatchRequest(
        @NotEmpty(message = "A lista de conteúdos não pode estar vazia")
        @Size(max = 100, message = "Máximo de 100 conteúdos por lote")
        @Valid
        List<ConteudoRequest> items
) {
}