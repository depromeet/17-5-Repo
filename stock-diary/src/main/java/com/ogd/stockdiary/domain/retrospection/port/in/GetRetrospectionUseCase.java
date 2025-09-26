package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;

public interface GetRetrospectionUseCase {

    GetRetrospectionResponse getRetrospection(Long retrospectionId, Long userId);
}
