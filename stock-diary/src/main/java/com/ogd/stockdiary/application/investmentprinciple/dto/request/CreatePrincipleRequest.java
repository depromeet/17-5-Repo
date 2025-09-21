package com.ogd.stockdiary.application.investmentprinciple.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "투자원칙 생성 요청 정보")
public class CreatePrincipleRequest {

  @Schema(description = "투자원칙 내용", example = "손절매는 반드시 10% 이내에서", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "투자원칙 내용은 필수입니다.")
  private String principle;
}
