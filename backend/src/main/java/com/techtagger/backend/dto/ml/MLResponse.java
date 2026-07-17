package com.techtagger.backend.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record MLResponse(

        String category,
        double probability,

        @JsonProperty("top_categories")
        List<CategoryScore> topCategories,

        List<String> keywords,

        @JsonProperty("related_content")
        List<RelatedContent> relatedContent,

        @JsonProperty("processed_at")
        LocalDateTime processedAt

) {}