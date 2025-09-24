package com.ogd.stockdiary.application.report.repository;

import org.springframework.stereotype.Component;

import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final JpaFeedbackRepository jpaFeedbackRepository;

    public Feedback save(Feedback feedback) {
        return jpaFeedbackRepository.save(feedback);
    }
}
