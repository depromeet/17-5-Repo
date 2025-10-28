package com.ogd.stockdiary.application.investmentprinciple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투자원칙 생성 요청 정보")
public class CreatePrincipleRequest {

    @Schema(description = "투자원칙 그룹 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "투자원칙 그룹 ID는 필수입니다.")
    private Long groupId;

    @Schema(description = "투자원칙 내용", example = "손절매는 반드시 10% 이내에서", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "투자원칙 내용은 필수입니다.")
    private String principle;

    @Schema(description = "그룹 내 표시 순서 (선택사항)", example = "0")
    private Integer displayOrder;
}
