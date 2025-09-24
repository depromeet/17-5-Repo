package com.ogd.stockdiary.domain.report.entity;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import jakarta.persistence.*;
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

    @Column(nullable = false, length = 200)
    private String feedback;

    @OneToOne(fetch = FetchType.LAZY)
    private Retrospection retrospection;

    public Feedback(String feedback, Retrospection retrospection) {
        this.feedback = feedback;
        this.retrospection = retrospection;
    }
}
