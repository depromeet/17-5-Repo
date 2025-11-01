package com.ogd.stockdiary.application.retrospection.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.retrospection.entity.Memo;

public interface JpaMemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findByRetrospectionIdOrderByIdDesc(Long retrospectionId);
}
