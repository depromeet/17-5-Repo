package com.ogd.stockdiary.application.principlegroup.dto.response;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "기본 투자원칙 그룹 응답 정보 (간소화)")
public class DefaultPrincipleGroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private final Long id;

    @Schema(description = "그룹명", example = "기본 매수 원칙")
    private final String groupName;

    @Schema(description = "썸네일 (이모지 또는 이미지 URL)", example = "📈")
    private final String thumbnail;

    @Schema(description = "투자원칙 타입 (BUY: 매수, SELL: 매도)", example = "BUY")
    private final PrincipleType principleType;
}
