package com.ogd.stockdiary.application.report.repository;

import org.springframework.stereotype.Component;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;
import com.ogd.stockdiary.domain.report.port.out.RetrospectionForReportRepository;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RetrospectionForReportRepositoryImpl implements RetrospectionForReportRepository {

    private final JpaRetrospectionForReportRepository jpaRetrospectionForReportRepository;

    @Override
    public RetrospectionForReport getById(Long id) {
        RetrospectionForReport report = jpaRetrospectionForReportRepository
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003, "회고를 찾을 수 없습니다" + id));

        return report;
    }
}
