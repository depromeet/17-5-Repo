package com.ogd.stockdiary.application.retrospection.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "메모 응답 정보")
public class MemoResponse {

    @Schema(description = "메모 ID", example = "1")
    private Long memoId;

    @Schema(description = "메모 내용", example = "이번 거래에서 감정적으로 판단한 부분이 있었다.")
    private String content;

    @Schema(description = "메모 생성일시", example = "2025-09-14T10:00:00")
    private LocalDateTime createdAt;
}
