package com.ogd.stockdiary.domain.retrospection.port.out;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import java.util.List;
import java.util.Optional;

public interface RetrospectionRepository {

    Retrospection save(Retrospection retrospection);

    Optional<Retrospection> findById(Long id);

    List<Retrospection> findByUserId(Long userId);
}