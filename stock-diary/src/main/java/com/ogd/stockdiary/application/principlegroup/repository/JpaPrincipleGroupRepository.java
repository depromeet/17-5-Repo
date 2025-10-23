package com.ogd.stockdiary.application.principlegroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;

public interface JpaPrincipleGroupRepository extends JpaRepository<PrincipleGroup, Long> {

    List<PrincipleGroup> findByUserId(Long userId);

    Optional<PrincipleGroup> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
