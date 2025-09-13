package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRetrospectionCommand {

  private final Long userId;
  private final String symbol;
  private final String market;
  private final OrderType orderType;
  private final BigDecimal price;
  private final Currency currency;
  private final Integer volume;
  private final LocalDate orderDate;
  private final Double returnRate;
}
