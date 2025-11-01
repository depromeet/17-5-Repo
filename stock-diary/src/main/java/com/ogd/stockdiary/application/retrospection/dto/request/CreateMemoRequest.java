package com.ogd.stockdiary.application.retrospection.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "메모 생성 요청 정보")
public class CreateMemoRequest {

    @Schema(description = "메모 내용", example = "이번 거래에서 감정적으로 판단한 부분이 있었다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "메모 내용은 필수입니다")
    private String content;
}
