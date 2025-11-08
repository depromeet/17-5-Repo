package com.ogd.stockdiary.application.retrospection.dto.response;

import java.util.List;

public record MarketGroupResponse(
    String companyName,
    List<RetrospectionDetailResponse> retrospections) {
}
