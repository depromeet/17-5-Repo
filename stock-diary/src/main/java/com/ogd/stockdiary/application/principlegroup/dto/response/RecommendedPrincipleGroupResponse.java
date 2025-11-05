package com.ogd.stockdiary.application.principlegroup.dto.response;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "추천 투자원칙 그룹 응답 정보 (유저명 및 원칙 개수 포함)")
public class RecommendedPrincipleGroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private final Long id;

    @Schema(description = "그룹명", example = "워렌 버핏의 가치투자 원칙")
    private final String groupName;

    @Schema(description = "투자원칙 타입 (BUY: 매수, SELL: 매도)", example = "BUY")
    private final PrincipleType principleType;

    @Schema(description = "썸네일 (이모지 또는 이미지 URL)", example = "📈")
    private final String thumbnail;

    @Schema(description = "그룹 표시 순서", example = "1")
    private final Integer displayOrder;

    @Schema(description = "그룹에 속한 투자원칙 개수", example = "5")
    private final Integer principleCount;

    @Schema(description = "투자자/전문가 이름", example = "워렌 버핏")
    private final String userName;
}
