package com.techtagger.backend.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MLBatchResponseItem(
        String title,
        String category,
        double probability,
        @JsonProperty("top_categories") List<CategoryScore> topCategories,
        List<String> keywords,
        @JsonProperty("related_content") List<RelatedContent> relatedContent
) {
}