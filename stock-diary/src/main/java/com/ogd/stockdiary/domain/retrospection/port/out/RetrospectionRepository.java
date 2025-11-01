package com.ogd.stockdiary.domain.retrospection.port.out;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

public interface RetrospectionRepository {

    Retrospection save(Retrospection retrospection);

    Retrospection getById(Long id);

    Retrospection findByIdAndUserId(Long id, Long userId);

    void deleteById(Long id);
}
