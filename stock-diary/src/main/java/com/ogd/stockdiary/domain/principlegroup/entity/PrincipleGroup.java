package com.ogd.stockdiary.domain.principlegroup.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

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
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(nullable = false)
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "principle_type", nullable = false)
    private PrincipleType principleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private PrincipleGroupType groupType;

    @Column(nullable = false)
    private String thumbnail;

    private Integer displayOrder;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.groupType == null) {
            this.groupType = PrincipleGroupType.USER;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static PrincipleGroup create(User user, String groupName, PrincipleType principleType,
        String thumbnail, Integer displayOrder) {
        PrincipleGroup principleGroup = new PrincipleGroup();
        principleGroup.user = user;
        principleGroup.groupName = groupName;
        principleGroup.principleType = principleType;
        principleGroup.groupType = PrincipleGroupType.USER;
        principleGroup.thumbnail = thumbnail;
        principleGroup.displayOrder = displayOrder;
        return principleGroup;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updatePrincipleType(PrincipleType principleType) {
        this.principleType = principleType;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
