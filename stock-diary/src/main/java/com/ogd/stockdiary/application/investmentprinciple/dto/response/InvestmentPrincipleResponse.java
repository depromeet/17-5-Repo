package com.ogd.stockdiary.application.investmentprinciple.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvestmentPrincipleResponse {

    private final Long id;
    private final Long groupId;
    private final String principle;
    private final Integer displayOrder;
}
