package com.ogd.stockdiary.application.retrospection.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

public interface JpaRetrospectionRepository extends JpaRepository<Retrospection, Long> {

    Optional<Retrospection> findByIdAndUserId(Long id, Long userId);

    // N+1 문제 해결: User와 Feedback을 페치 조인으로 한번에 조회
    @Query("SELECT DISTINCT r FROM Retrospection r " +
        "LEFT JOIN FETCH r.user " +
        "LEFT JOIN FETCH r.feedback " +
        "WHERE r.user.id = :userId")
    List<Retrospection> findAllByUserId(@Param("userId") Long userId);

}
