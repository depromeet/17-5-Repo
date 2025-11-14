package com.ogd.stockdiary.domain.retrospection.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retrospections")
@Getter
@NoArgsConstructor
public class Retrospection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToOne(mappedBy = "retrospection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedback;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String market;

    @Embedded
    private Order order;

    private Double returnRate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Retrospection(
        User user,
        String symbol,
        String market,
        Order order,
        Double returnRate) {
        this.user = user;
        this.symbol = symbol;
        this.market = market;
        this.order = order;
        this.returnRate = returnRate;
    }
}
