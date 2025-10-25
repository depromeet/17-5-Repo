package com.ogd.stockdiary.application.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.image.entity.ImageMetadata;

@Repository
public interface JpaImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {

}
