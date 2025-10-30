package com.ogd.stockdiary.domain.report.port.out;

import java.util.List;

import com.ogd.stockdiary.domain.report.vo.ReportSourceData;

public interface ReportDataPort {
    List<ReportSourceData> findByRetrospectionId(Long retrospectionId);

}
