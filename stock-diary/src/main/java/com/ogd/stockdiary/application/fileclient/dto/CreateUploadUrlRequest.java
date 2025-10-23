package com.ogd.stockdiary.application.fileclient.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업로드 URL 생성 요청 DTO
 *
 * <p><b>⚠️ 테스트 전용 - 추후 삭제 예정</b>
 */
@Getter
@NoArgsConstructor
public class CreateUploadUrlRequest {

    @NotBlank(message = "objectKey는 필수입니다")
    private String objectKey;

    @Min(value = 60, message = "TTL은 최소 60초 이상이어야 합니다")
    private int ttl = 3600; // 기본 1시간
}
