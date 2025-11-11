package com.ogd.stockdiary.domain.report.port.out;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface FeedbackRepository {

    Feedback save(Feedback feedback);

    void deleteById(Long id);

    List<Feedback> findAllByUserId(Long userId);

    Optional<Feedback> findByRetrospectionId(Long retrospectionId);
}
