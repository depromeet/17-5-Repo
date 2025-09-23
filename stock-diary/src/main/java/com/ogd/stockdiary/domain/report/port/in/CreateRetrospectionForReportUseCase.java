package com.ogd.stockdiary.domain.report.port.in;

import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;

public interface CreateRetrospectionForReportUseCase {

    RetrospectionForReport createRetrospectionForReport(CreateRetrospectionForReportCommand command);
}
