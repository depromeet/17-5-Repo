package com.ogd.stockdiary.application.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface JpaFeedbackRepository extends JpaRepository<Feedback, Long> {}
