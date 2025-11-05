package com.ogd.stockdiary.application.principlegroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroupType;

public interface JpaPrincipleGroupRepository extends JpaRepository<PrincipleGroup, Long> {

    List<PrincipleGroup> findByUserId(Long userId);

    List<PrincipleGroup> findByUserIdAndGroupType(Long userId, PrincipleGroupType groupType);

    List<PrincipleGroup> findByGroupType(PrincipleGroupType groupType);

    @Query("SELECT pg FROM PrincipleGroup pg JOIN FETCH pg.user WHERE pg.groupType = :groupType")
    List<PrincipleGroup> findByGroupTypeWithUser(@Param("groupType") PrincipleGroupType groupType);

    Optional<PrincipleGroup> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
