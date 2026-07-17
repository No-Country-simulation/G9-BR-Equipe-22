package com.techtagger.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConteudoRequest(

        @NotBlank(message = "Título não pode ser vazio")
        @Size(min = 3, max = 500, message = "Título deve ter entre 3 e 500 caracteres")
        String titulo,

        @NotBlank(message = "Texto não pode ser vazio")
        @Size(min = 10, max = 10000, message = "Texto deve ter entre 10 e 10000 caracteres")
        String texto

) {}