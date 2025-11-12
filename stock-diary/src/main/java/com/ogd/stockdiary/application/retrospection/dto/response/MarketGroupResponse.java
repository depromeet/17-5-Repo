package com.ogd.stockdiary.application.retrospection.dto.response;

import java.util.List;

import com.ogd.stockdiary.domain.stock.entity.Market;

public record MarketGroupResponse(
    String companyName,
    String image,
    String symbol,
    Market market,
    List<RetrospectionDetailResponse> retrospections) {
}
