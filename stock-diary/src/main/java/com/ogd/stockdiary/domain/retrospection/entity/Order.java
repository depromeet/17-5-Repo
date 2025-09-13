package com.ogd.stockdiary.domain.retrospection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "retrospection_id", nullable = false)
  private Retrospection retrospection;

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

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Order(
      Retrospection retrospection,
      OrderType orderType,
      BigDecimal price,
      Currency currency,
      Integer volume,
      LocalDate orderDate) {
    validatePrice(price);
    validateVolume(volume);

    this.retrospection = retrospection;
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
