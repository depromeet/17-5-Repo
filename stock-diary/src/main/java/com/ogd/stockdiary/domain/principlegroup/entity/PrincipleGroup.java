package com.ogd.stockdiary.domain.principlegroup.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;
import com.ogd.stockdiary.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principle_groups")
@Getter
@NoArgsConstructor
public class PrincipleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "principle_type", nullable = false)
    private PrincipleType principleType;

    @Column(nullable = false)
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

    public static PrincipleGroup create(User user, String groupName, PrincipleType principleType,
        Integer displayOrder) {
        PrincipleGroup principleGroup = new PrincipleGroup();
        principleGroup.user = user;
        principleGroup.groupName = groupName;
        principleGroup.principleType = principleType;
        principleGroup.displayOrder = displayOrder;
        return principleGroup;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updatePrincipleType(PrincipleType principleType) {
        this.principleType = principleType;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
