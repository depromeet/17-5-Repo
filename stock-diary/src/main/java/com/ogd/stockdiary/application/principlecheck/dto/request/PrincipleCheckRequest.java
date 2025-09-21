package com.ogd.stockdiary.application.principlecheck.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투자원칙 체크 요청 정보")
public class PrincipleCheckRequest {

  @Schema(description = "투자원칙 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "투자원칙 ID는 필수입니다") private Long principleId;

  @Schema(description = "원칙 준수 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "원칙 준수 여부는 필수입니다") private Boolean isFollowed;
}
