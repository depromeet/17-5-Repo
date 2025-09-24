package com.ogd.stockdiary.domain.analysis.dto;

public record AnalysisResponse(
        String text
) {

    public AnalysisResponse toResponse() {
        return new AnalysisResponse(text);
    }
}
