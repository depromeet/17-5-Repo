package com.ogd.stockdiary.domain.principlecheck.entity;

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

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principle_checks")
@Getter
@NoArgsConstructor
public class PrincipleCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospection_id", nullable = false)
    private Retrospection retrospection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principle_id", nullable = false)
    private InvestmentPrinciple principle;

    @Column(name = "is_followed", nullable = false)
    private Boolean isFollowed;

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

    public static PrincipleCheck create(
        Retrospection retrospection, InvestmentPrinciple principle, Boolean isFollowed) {
        PrincipleCheck principleCheck = new PrincipleCheck();
        principleCheck.retrospection = retrospection;
        principleCheck.principle = principle;
        principleCheck.isFollowed = isFollowed;
        return principleCheck;
    }
}
