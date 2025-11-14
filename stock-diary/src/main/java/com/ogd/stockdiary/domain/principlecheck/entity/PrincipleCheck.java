package com.ogd.stockdiary.domain.principlecheck.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principle_checks", indexes = {
    @Index(name = "ix_principle_checks_retrospection_id", columnList = "retrospection_id")
})
@Getter
@NoArgsConstructor
public class PrincipleCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospection_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Retrospection retrospection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principle_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private InvestmentPrinciple principle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrincipleCheckStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

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
        Retrospection retrospection, InvestmentPrinciple principle, PrincipleCheckStatus status, String reason) {
        PrincipleCheck principleCheck = new PrincipleCheck();
        principleCheck.retrospection = retrospection;
        principleCheck.principle = principle;
        principleCheck.status = status;
        principleCheck.reason = reason;
        return principleCheck;
    }
}
