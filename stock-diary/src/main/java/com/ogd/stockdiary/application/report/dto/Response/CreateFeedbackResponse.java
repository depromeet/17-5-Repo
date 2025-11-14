package com.ogd.stockdiary.application.report.dto.Response;

import java.math.BigDecimal;
import java.util.List;

import com.ogd.stockdiary.domain.retrospection.entity.OrderType;

import lombok.Builder;

@Builder
public record CreateFeedbackResponse(
    String companyName,
    String symbol,
    BigDecimal price,
    Integer volume,
    OrderType orderType,
    String companyLogo,

    long keptCount,
    long neutralCount,
    long notKeptCount,

    String badge,
    List<String> keep,
    List<String> fix,
    List<String> next) {
}
