package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;

public interface CreateRetrospectionUseCase {

  Retrospection createRetrospection(CreateRetrospectionCommand command);
}
