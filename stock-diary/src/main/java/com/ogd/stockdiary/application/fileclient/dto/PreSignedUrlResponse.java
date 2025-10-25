package com.ogd.stockdiary.application.fileclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Pre-Signed URL 응답 DTO
 *
 * <p>
 * <b>⚠️ 테스트 전용 - 추후 삭제 예정</b>
 */
@Getter
@AllArgsConstructor
public class PreSignedUrlResponse {

    private String url;
    private String objectKey;
    private int ttl;
}
