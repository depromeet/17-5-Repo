package com.ogd.stockdiary.domain.investmentprinciple.dto;

import java.util.List;

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BatchProcessResult {

    private final List<InvestmentPrinciple> createdPrinciples;
    private final List<InvestmentPrinciple> updatedPrinciples;
    private final List<Long> deletedPrincipleIds;
}
