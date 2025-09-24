package com.ogd.stockdiary.application.report.repository;


import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final JpaFeedbackRepository jpaFeedbackRepository;

    public Feedback save(Feedback feedback){
        return jpaFeedbackRepository.save(feedback);
    }
}
