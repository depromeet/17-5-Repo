package com.ogd.stockdiary.domain.retrospection.port.in;

import java.util.List;

import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.MarketGroupResponse;

public interface GetRetrospectionUseCase {

    GetRetrospectionResponse getRetrospection(Long retrospectionId, Long userId);

    List<MarketGroupResponse> getAllRetrospections(GetRetrospectionCommand command);
}
