package com.ogd.stockdiary.application.image.repository;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.image.entity.ImageMetadata;
import com.ogd.stockdiary.domain.image.port.out.ImageRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

    private final JpaImageMetadataRepository jpaImageMetadataRepository;

    @Override
    public ImageMetadata save(ImageMetadata imageMetadata) {
        return jpaImageMetadataRepository.save(imageMetadata);
    }
}
