package com.ogd.stockdiary.domain.investmentprinciple.dto;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreatePrincipleCommand {

    private final Long userId;
    private final Long groupId;
    private final PrincipleType principleType;
    private final String principle;
    private final Integer displayOrder;
}
