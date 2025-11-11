package com.ogd.stockdiary.application.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface JpaFeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findAllByUserId(Long userId);

    Optional<Feedback> findByRetrospectionId(Long retrospectionId);
}
