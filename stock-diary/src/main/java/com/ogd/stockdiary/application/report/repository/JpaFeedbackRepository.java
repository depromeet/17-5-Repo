package com.ogd.stockdiary.application.report.repository;

import com.ogd.stockdiary.domain.report.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JpaFeedbackRepository extends JpaRepository<Feedback, Long> {
}
