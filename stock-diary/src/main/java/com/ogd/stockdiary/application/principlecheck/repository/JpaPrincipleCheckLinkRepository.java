package com.ogd.stockdiary.application.principlecheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckLink;

public interface JpaPrincipleCheckLinkRepository extends JpaRepository<PrincipleCheckLink, Long> {

    List<PrincipleCheckLink> findByPrincipleCheckId(Long principleCheckId);
}
