package com.ogd.stockdiary.application.principlecheck.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @Schema(description = "투자원칙 준수 상태 (KEPT: 지켰어요, NEUTRAL: 보통이에요, NOT_KEPT: 안지켰어요)", example = "KEPT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "투자원칙 준수 상태는 필수입니다")
    private PrincipleCheckStatus status;

    @Schema(description = "투자 이유", example = "기술적 분석 결과 상승 추세가 확인되어 매수했습니다.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @Schema(description = "이미지 ID 목록 (최대 3개)", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 3, message = "이미지는 최대 3개까지 등록할 수 있습니다")
    private List<Long> imageIds;

    @Schema(description = "링크 목록 (최대 3개)", example = "[\"https://example.com/article1\", \"https://example.com/article2\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 3, message = "링크는 최대 3개까지 등록할 수 있습니다")
    private List<String> links;
}
