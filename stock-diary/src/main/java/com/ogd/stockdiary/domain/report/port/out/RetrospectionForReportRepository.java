package com.ogd.stockdiary.domain.report.port;

import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;

public interface RetrospectionForReportRepository {

    RetrospectionForReport getById(Long id);
}
