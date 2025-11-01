package com.ogd.stockdiary.application.investmentprinciple.dto.response;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvestmentPrincipleResponse {

    private final Long id;
    private final Long groupId;
    private final String groupName;
    private final PrincipleType principleType;
    private final String principle;
    private final String description;
    private final Integer displayOrder;
}
