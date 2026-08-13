package com.techtagger.backend.dto.response;

import java.util.List;

public record ConteudoBatchResponse(int total, List<ConteudoResponse> results) {
}