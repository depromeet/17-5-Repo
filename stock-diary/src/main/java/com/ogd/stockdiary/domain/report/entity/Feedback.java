package com.ogd.stockdiary.domain.report.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedbacks")
@NoArgsConstructor
@Getter
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String title;

    @Column(columnDefinition = "json", nullable = true)
    private String keep;

    @Column(columnDefinition = "json", nullable = true)
    private String improve;

    @Column(columnDefinition = "json", nullable = true)
    private String nextTime;

    @OneToOne(fetch = FetchType.LAZY)
    private Retrospection retrospection;

    @Column(nullable = true)
    private long keptCount;

    @Column(nullable = true)
    private long neutralCount;

    @Column(nullable = true)
    private long notKeptCount;

    @Column(nullable = true)
    private String symbol;

    @Column(nullable = true)
    private BigDecimal price;

    @Column(nullable = true)
    private Integer volume;

    @Column(nullable = true)
    private OrderType orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Feedback(
        String title,
        String keep,
        String improve,
        String nextTime,
        Retrospection retrospection,
        long keptCount,
        long neutralCount,
        long notKeptCount,
        String symbol,
        BigDecimal price,
        Integer volume,
        OrderType orderType,
        User user) {
        this.title = title;
        this.keep = keep;
        this.improve = improve;
        this.nextTime = nextTime;
        this.retrospection = retrospection;
        this.keptCount = keptCount;
        this.neutralCount = neutralCount;
        this.notKeptCount = notKeptCount;
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.orderType = orderType;
        this.user = user;
    }

}
