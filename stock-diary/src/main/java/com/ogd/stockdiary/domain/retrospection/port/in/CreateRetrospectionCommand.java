package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRetrospectionCommand(
    Long userId,
    String symbol,
    String market,
    OrderType orderType,
    BigDecimal price,
    Currency currency,
    Integer volume,
    LocalDate orderDate,
    Double returnRate
) {}