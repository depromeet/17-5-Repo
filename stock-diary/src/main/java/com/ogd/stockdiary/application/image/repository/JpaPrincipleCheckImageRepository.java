package com.ogd.stockdiary.application.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.image.entity.PrincipleCheckImage;

@Repository
public interface JpaPrincipleCheckImageRepository extends JpaRepository<PrincipleCheckImage, Long> {

    List<PrincipleCheckImage> findByPrincipleCheckId(Long principleCheckId);
}
