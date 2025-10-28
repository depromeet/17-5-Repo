package com.ogd.stockdiary.application.principlegroup.dto.response;

import java.util.List;

import com.ogd.stockdiary.application.investmentprinciple.dto.response.InvestmentPrincipleResponse;
import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투자원칙 그룹 응답 정보")
public class PrincipleGroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private final Long id;

    @Schema(description = "그룹명", example = "🔥 이건 좀 지키자 제발")
    private final String groupName;

    @Schema(description = "투자원칙 타입 (BUY: 매수, SELL: 매도)", example = "BUY")
    private final PrincipleType principleType;

    @Schema(description = "그룹 표시 순서", example = "1")
    private final Integer displayOrder;

    @Schema(description = "그룹에 속한 투자원칙 목록")
    private final List<InvestmentPrincipleResponse> principles;
}
