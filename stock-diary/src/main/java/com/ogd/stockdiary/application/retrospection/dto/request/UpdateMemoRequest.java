package com.ogd.stockdiary.application.retrospection.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "메모 수정 요청 정보")
public class UpdateMemoRequest {

    @Schema(description = "메모 내용", example = "수정된 메모 내용입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "메모 내용은 필수입니다")
    private String content;
}
