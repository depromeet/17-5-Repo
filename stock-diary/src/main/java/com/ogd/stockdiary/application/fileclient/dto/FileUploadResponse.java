package com.ogd.stockdiary.application.fileclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 파일 업로드 응답 DTO
 *
 * <p>
 * <b>⚠️ 테스트 전용 - 추후 삭제 예정</b>
 */
@Getter
@AllArgsConstructor
public class FileUploadResponse {

    private String objectKey;
    private long fileSize;
    private String message;
}
