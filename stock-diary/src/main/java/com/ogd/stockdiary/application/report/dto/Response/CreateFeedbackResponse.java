package com.ogd.stockdiary.application.report.dto.Response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateFeedbackResponse(
        @JsonProperty("요약 한 마디") String summerizedFeedback,
        @JsonProperty("당시 시장 현황") String market,
        @JsonProperty("AI 추천 원칙") List<Map<String, String>> principles) {}
