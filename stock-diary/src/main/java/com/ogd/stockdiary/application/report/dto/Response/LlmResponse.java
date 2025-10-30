package com.ogd.stockdiary.application.report.dto.Response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LlmResponse(
    @JsonProperty("뱃지") String title,
    @JsonProperty("앞으로도 유지해보세요") List<String> keep,
    @JsonProperty("고쳐보면 좋아요") List<String> improve,
    @JsonProperty("다음 투자엔 이렇게 해보세요") List<String> nextTime) {
}
