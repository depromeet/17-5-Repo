package com.ogd.stockdiary.application.principlecheck.dto.request;

import jakarta.validation.constraints.NotNull;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투자원칙 체크 요청 정보")
public class PrincipleCheckRequest {

    @Schema(description = "투자원칙 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "투자원칙 ID는 필수입니다")
    private Long principleId;

    @Schema(description = "투자원칙 준수 상태 (KEPT: 지켰어요, NEUTRAL: 보통이에요, NOT_KEPT: 안지켰어요)", example = "KEPT", allowableValues = {
        "KEPT", "NEUTRAL", "NOT_KEPT"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "투자원칙 준수 상태는 필수입니다")
    private PrincipleCheckStatus status;
}
