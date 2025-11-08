package com.ogd.stockdiary.domain.image.port.out;

import java.util.Optional;

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

    /**
     * ID로 이미지 메타데이터를 조회합니다.
     *
     * @param id
     *            이미지 메타데이터 ID
     * @return 조회된 이미지 메타데이터 (존재하지 않으면 Optional.empty())
     */
    Optional<ImageMetadata> findById(Long id);
}
