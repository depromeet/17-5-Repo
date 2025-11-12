package com.ogd.stockdiary.domain.report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.ogd.stockdiary.domain.retrospection.entity.Order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retrospections")
@NoArgsConstructor
@Getter
public class RetrospectionForReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String market;

    private String content;

    @Embedded
    private Order order;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public RetrospectionForReport(String symbol, String market, Order order, String content, LocalDateTime createdAt) {
        this.symbol = symbol;
        this.market = market;
        this.order = order;
        this.content = content;
        this.createdAt = createdAt;
    }
}
