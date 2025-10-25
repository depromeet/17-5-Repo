package com.ogd.stockdiary.application.principlecheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckLink;

public interface JpaPrincipleCheckLinkRepository extends JpaRepository<PrincipleCheckLink, Long> {
}
