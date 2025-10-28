package com.ogd.stockdiary.application.principlegroup.dto.request;

import jakarta.validation.constraints.NotBlank;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투자원칙 그룹 수정 요청 정보")
public class UpdatePrincipleGroupRequest {

    @Schema(description = "그룹명 (이모지 포함 가능)", example = "💪 초보자를 위한 매도 원칙", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "그룹명은 필수입니다")
    private String groupName;

    @Schema(description = "투자원칙 타입 (BUY: 매수, SELL: 매도) - 선택사항", example = "SELL")
    private PrincipleType principleType;
}
