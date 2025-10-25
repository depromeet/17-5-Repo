package com.ogd.stockdiary.domain.image.entity;

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

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principle_check_images")
@Getter
@NoArgsConstructor
public class PrincipleCheckImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principle_check_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private PrincipleCheck principleCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ImageMetadata image;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static PrincipleCheckImage create(PrincipleCheck principleCheck, ImageMetadata image) {
        PrincipleCheckImage principleCheckImage = new PrincipleCheckImage();
        principleCheckImage.principleCheck = principleCheck;
        principleCheckImage.image = image;
        return principleCheckImage;
    }
}
