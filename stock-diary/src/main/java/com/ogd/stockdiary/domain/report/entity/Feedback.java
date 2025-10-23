package com.ogd.stockdiary.domain.report.entity;

import jakarta.persistence.*;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

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

    @Column(nullable = false, length = 500)
    private String feedback;

    @Column(nullable = true, length = 500)
    private String summerizedFeedback;

    @Column(nullable = true)
    private String market;

    @Column(columnDefinition = "json", nullable = true)
    private String principles;

    @OneToOne(fetch = FetchType.LAZY)
    private Retrospection retrospection;

    @Builder
    public Feedback(
        String feedback,
        String summerizedFeedback,
        String market,
        String principles,
        Retrospection retrospection) {
        this.feedback = feedback;
        this.summerizedFeedback = summerizedFeedback;
        this.market = market;
        this.principles = principles;
        this.retrospection = retrospection;
    }
}
