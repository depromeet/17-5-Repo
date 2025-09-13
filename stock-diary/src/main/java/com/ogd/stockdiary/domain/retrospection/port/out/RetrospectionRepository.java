package com.ogd.stockdiary.domain.retrospection.port.out;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import java.util.List;

public interface RetrospectionRepository {

  Retrospection save(Retrospection retrospection);

  Retrospection findById(Long id);

  List<Retrospection> findByUserId(Long userId);
}
