package com.techtagger.backend.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtagger.backend.dto.ml.RelatedContent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class RelatedContentListConverter implements AttributeConverter<List<RelatedContent>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RelatedContent> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar relacionados", e);
        }
    }

    @Override
    public List<RelatedContent> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return Collections.emptyList();
        try {
            return MAPPER.readValue(dbData, new TypeReference<List<RelatedContent>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar relacionados", e);
        }
    }
}