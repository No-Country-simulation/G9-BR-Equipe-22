package com.techtagger.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARADOR = ";;";

    @Override
    public String convertToDatabaseColumn(List<String> lista) {
        if (lista == null || lista.isEmpty()) return "";
        return String.join(SEPARADOR, lista);
    }

    @Override
    public List<String> convertToEntityAttribute(String dado) {
        if (dado == null || dado.isEmpty()) return List.of();
        return Arrays.stream(dado.split(SEPARADOR))
                .collect(Collectors.toList());
    }
}
