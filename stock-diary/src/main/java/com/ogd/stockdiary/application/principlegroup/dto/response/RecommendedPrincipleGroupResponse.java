package com.ogd.stockdiary.application.principlegroup.dto.response;

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

    @Schema(description = "썸네일 (이모지 또는 이미지 URL)", example = "📈")
    private final String thumbnail;

    @Schema(description = "그룹에 속한 투자원칙 개수", example = "5")
    private final Integer principleCount;
}
