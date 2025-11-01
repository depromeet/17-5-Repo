package com.ogd.stockdiary.application.report.dto.Response;

public record BadgeResponse(
    int hedge,
    int bronze,
    int silver,
    int gold,
    int percentage) {
}
