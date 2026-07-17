package com.techtagger.backend.dto.ml;

public record RelatedContent(
        String title,
        String category,
        double similarity,
        String url
) {}