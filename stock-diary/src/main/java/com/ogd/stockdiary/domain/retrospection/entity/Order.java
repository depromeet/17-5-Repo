package com.ogd.stockdiary.domain.retrospection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Order {

  @Enumerated(EnumType.STRING)
  @Column(name = "order_type", nullable = false)
  private OrderType orderType;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Currency currency;

  @Column(nullable = false)
  private Integer volume;

  @Column(name = "order_date", nullable = false)
  private LocalDate orderDate;

  public Order(
      OrderType orderType,
      BigDecimal price,
      Currency currency,
      Integer volume,
      LocalDate orderDate) {
    validatePrice(price);
    validateVolume(volume);

    this.orderType = orderType;
    this.price = price;
    this.currency = currency;
    this.volume = volume;
    this.orderDate = orderDate;
  }

  private void validatePrice(BigDecimal price) {
    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("가격은 0보다 큰 값이어야 합니다.");
    }
  }

  private void validateVolume(Integer volume) {
    if (volume == null || volume <= 0) {
      throw new IllegalArgumentException("거래량은 0보다 큰 값이어야 합니다.");
    }
  }
}
