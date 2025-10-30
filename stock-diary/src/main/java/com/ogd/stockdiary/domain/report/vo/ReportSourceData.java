package com.ogd.stockdiary.domain.report.vo;

import java.util.List;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;

// 프롬프트에 전달할 정보만 담은 vo
public record ReportSourceData(

    // 투자 원칙 체크 정보
    Long principleCheckId,
    String principle,
    PrincipleCheckStatus status,
    String reason,

    // 이미지 정보
    List<String> imageUrls

) {
}
