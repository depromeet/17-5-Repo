package com.ogd.stockdiary.domain.principlecheck.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principle_check_links")
@Getter
@NoArgsConstructor
public class PrincipleCheckLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principle_check_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private PrincipleCheck principleCheck;

    @Column(name = "link_url", nullable = false, length = 2048)
    private String linkUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static PrincipleCheckLink create(PrincipleCheck principleCheck, String linkUrl) {
        PrincipleCheckLink principleCheckLink = new PrincipleCheckLink();
        principleCheckLink.principleCheck = principleCheck;
        principleCheckLink.linkUrl = linkUrl;
        return principleCheckLink;
    }
}
