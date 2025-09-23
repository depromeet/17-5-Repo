package com.ogd.stockdiary.domain.investmentprinciple.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdatePrincipleCommand {

    private final Long principleId;
    private final Long userId;
    private final String principle;
}
