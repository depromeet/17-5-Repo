package com.ogd.stockdiary.domain.investmentprinciple.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investment_principles")
@Getter
@NoArgsConstructor
public class InvestmentPrinciple {

    public static final int MAX_PRINCIPLES_PER_GROUP = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private PrincipleGroup principleGroup;

    private String principle;

    @Column(name = "display_order")
    private Integer displayOrder;

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

    public static InvestmentPrinciple create(
        User user, PrincipleGroup principleGroup, String principle, Integer displayOrder) {
        InvestmentPrinciple investmentPrinciple = new InvestmentPrinciple();
        investmentPrinciple.user = user;
        investmentPrinciple.principleGroup = principleGroup;
        investmentPrinciple.principle = principle;
        investmentPrinciple.displayOrder = displayOrder;
        return investmentPrinciple;
    }

    public void updatePrinciple(String principle) {
        this.principle = principle;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
