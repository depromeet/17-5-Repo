package com.ogd.stockdiary.application.retrospection.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

public record RetrospectionDetailResponse(
    Long id,
    OrderType orderType,
    BigDecimal price,
    Integer volume,
    LocalDateTime retrospectionCreatedAt,
    LocalDate orderCreatedAt

) {
    public static RetrospectionDetailResponse fromEntity(Retrospection retrospection) {
        return new RetrospectionDetailResponse(
            retrospection.getId(),
            retrospection.getOrder().getOrderType(),
            retrospection.getOrder().getPrice(),
            retrospection.getOrder().getVolume(),
            retrospection.getCreatedAt(),
            retrospection.getOrder().getOrderDate());
    }
}
