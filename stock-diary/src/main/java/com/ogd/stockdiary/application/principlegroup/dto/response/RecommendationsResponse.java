package com.ogd.stockdiary.application.principlegroup.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "추천 및 기본 투자원칙 그룹 응답")
public class RecommendationsResponse {

    @Schema(description = "추천 투자원칙 그룹 목록 (유저명 및 원칙 개수 포함)")
    private final List<RecommendedPrincipleGroupResponse> recommended;

    @Schema(description = "기본 투자원칙 그룹 목록 (간소화)")
    private final List<DefaultPrincipleGroupResponse> defaults;
}
