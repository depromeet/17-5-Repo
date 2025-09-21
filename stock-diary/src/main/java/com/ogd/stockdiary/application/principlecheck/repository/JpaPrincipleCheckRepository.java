package com.ogd.stockdiary.application.principlecheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;

public interface JpaPrincipleCheckRepository extends JpaRepository<PrincipleCheck, Long> {

  List<PrincipleCheck> findByRetrospectionId(Long retrospectionId);

  void deleteByRetrospectionId(Long retrospectionId);
}
