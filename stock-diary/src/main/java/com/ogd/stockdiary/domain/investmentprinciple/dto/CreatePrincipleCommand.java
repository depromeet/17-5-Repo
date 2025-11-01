package com.ogd.stockdiary.domain.investmentprinciple.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreatePrincipleCommand {

    private final Long userId;
    private final Long groupId;
    private final String principle;
    private final String description;
    private final Integer displayOrder;
}
