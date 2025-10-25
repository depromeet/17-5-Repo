package com.ogd.stockdiary.domain.image.port.in;

import com.ogd.stockdiary.domain.image.dto.UploadImageCommand;
import com.ogd.stockdiary.domain.image.entity.ImageMetadata;

public interface ImageUseCase {

    /**
     * 이미지를 업로드하고 메타데이터를 저장합니다.
     *
     * @param command
     *            업로드 명령
     * @return 저장된 이미지 메타데이터
     */
    ImageMetadata uploadImage(UploadImageCommand command);

    /**
     * 이미지 다운로드 URL을 생성합니다.
     *
     * @param objectKey
     *            객체 키 (파일 경로)
     * @return Pre-Signed 다운로드 URL
     */
    String getDownloadUrl(String objectKey);
}
