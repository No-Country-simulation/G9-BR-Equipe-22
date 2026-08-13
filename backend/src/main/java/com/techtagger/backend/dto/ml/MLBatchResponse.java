package com.techtagger.backend.dto.ml;

import java.util.List;

public record MLBatchResponse(int total, List<MLBatchResponseItem> results) {
}