package com.ogd.stockdiary.domain.investmentprinciple.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BatchProcessCommand {

    private final Long userId;
    private final List<String> createPrinciples;
    private final List<UpdatePrincipleCommand> updatePrinciples;
    private final List<Long> deletePrincipleIds;
}
