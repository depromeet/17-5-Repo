package com.ogd.stockdiary.domain.retrospection.port.out;

import java.util.List;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

public interface RetrospectionRepository {

    Retrospection save(Retrospection retrospection);

    Retrospection getById(Long id);

    Retrospection findByIdAndUserId(Long id, Long userId);

    List<Retrospection> findAllByUserId(Long userId);

    void deleteById(Long id);
}
