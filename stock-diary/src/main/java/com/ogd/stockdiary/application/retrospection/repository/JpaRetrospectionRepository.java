package com.ogd.stockdiary.application.retrospection.repository;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRetrospectionRepository extends JpaRepository<Retrospection, Long> {

    List<Retrospection> findByUserId(Long userId);
}