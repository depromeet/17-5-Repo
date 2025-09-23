package com.ogd.stockdiary.application.investmentprinciple.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BatchProcessResponse {

    private final List<InvestmentPrincipleResponse> createdPrinciples;
    private final List<InvestmentPrincipleResponse> updatedPrinciples;
    private final List<Long> deletedPrincipleIds;
}
