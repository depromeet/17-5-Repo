package com.ogd.stockdiary.application.report.dto.Response;

import java.util.List;

public record LlmResponse(
    String badge,
    List<String> keep,
    List<String> fix,
    List<String> next) {
}
