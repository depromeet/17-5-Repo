package com.ogd.stockdiary.application.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;

public interface JpaRetrospectionForReportRepository
    extends
        JpaRepository<RetrospectionForReport, Long> {
}
