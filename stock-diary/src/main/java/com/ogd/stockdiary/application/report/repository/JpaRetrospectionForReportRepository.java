package com.ogd.stockdiary.application.report.repository;

import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRetrospectionForReportRepository  extends JpaRepository<RetrospectionForReport, Long> {
}
