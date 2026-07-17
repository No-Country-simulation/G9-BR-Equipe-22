package com.techtagger.backend.dto.response;

import com.techtagger.backend.dto.ml.RelatedContent;
import java.util.List;

public record ConteudoResponse(
        Long id,
        String categoria,
        double probabilidade,
        List<String> keywords,
        List<RelatedContent> relacionados
) {}