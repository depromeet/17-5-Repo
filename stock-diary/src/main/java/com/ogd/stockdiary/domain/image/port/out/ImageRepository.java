package com.ogd.stockdiary.domain.image.port.out;

import com.ogd.stockdiary.domain.image.entity.ImageMetadata;

public interface ImageRepository {

    /**
     * 이미지 메타데이터를 저장합니다.
     *
     * @param imageMetadata
     *            저장할 이미지 메타데이터
     * @return 저장된 이미지 메타데이터
     */
    ImageMetadata save(ImageMetadata imageMetadata);
}
