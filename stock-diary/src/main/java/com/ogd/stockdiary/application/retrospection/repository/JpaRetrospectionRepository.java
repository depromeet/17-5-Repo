package com.ogd.stockdiary.application.retrospection.repository;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRetrospectionRepository extends JpaRepository<Retrospection, Long> {

    Optional<Retrospection> findByIdAndUserId(Long id, Long userId);
}
