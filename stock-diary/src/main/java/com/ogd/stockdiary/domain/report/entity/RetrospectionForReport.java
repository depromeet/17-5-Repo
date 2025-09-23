package com.ogd.stockdiary.domain.report.entity;

import com.ogd.stockdiary.domain.retrospection.entity.Order;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retrospections")
@NoArgsConstructor
public class RetrospectionForReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String market;

    @Embedded private Order order;

    public RetrospectionForReport(String symbol, String market, Order order) {
        this.symbol = symbol;
        this.market = market;
        this.order = order;
    }
}
