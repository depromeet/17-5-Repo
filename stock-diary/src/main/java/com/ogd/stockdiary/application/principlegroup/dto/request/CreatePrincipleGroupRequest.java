package com.ogd.stockdiary.application.principlegroup.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투자원칙 그룹 생성 요청 정보")
public class CreatePrincipleGroupRequest {

    @Schema(description = "그룹명 (이모지 포함 가능)", example = "🔥 이건 좀 지키자 제발", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "그룹명은 필수입니다")
    private String groupName;

    @Schema(description = "그룹 표시 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "투자원칙 타입 (BUY: 매수, SELL: 매도)", example = "BUY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "투자원칙 타입은 필수입니다.")
    private PrincipleType principleType;

    @Schema(description = "그룹에 포함할 투자원칙 목록 (선택사항, 최대 5개)", example = "[\"안전마진을 확보하라\", \"기업의 본질 가치보다 낮게 거래되는 주식을 찾기\"]")
    @Size(max = 5, message = "그룹당 최대 5개의 투자원칙만 추가할 수 있습니다")
    private List<@NotBlank(message = "투자원칙 내용은 필수입니다") String> principles;
}
