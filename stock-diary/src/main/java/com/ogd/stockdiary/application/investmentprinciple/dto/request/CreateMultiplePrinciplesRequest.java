package com.ogd.stockdiary.application.investmentprinciple.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "다중 투자원칙 생성 요청 정보")
public class CreateMultiplePrinciplesRequest {

  @Schema(description = "투자원칙 목록", example = "[\"손절매는 반드시 10% 이내에서\", \"매수 전 기업 재무제표 분석 필수\", \"분산투자로 위험 관리\"]", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "투자원칙 목록은 필수입니다.") @NotEmpty(message = "투자원칙은 최소 1개 이상이어야 합니다.")
  private List<String> principles;
}
