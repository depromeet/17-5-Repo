package com.ogd.stockdiary.domain.investmentprinciple.usecase;

import java.util.List;

import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.ReorderPrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

public interface InvestmentPrincipleUseCase {

    List<InvestmentPrinciple> getUserPrinciples(Long userId, PrincipleType type);

    InvestmentPrinciple createPrinciple(CreatePrincipleCommand command);

    List<InvestmentPrinciple> createMultiplePrinciples(CreateMultiplePrinciplesCommand command);

    InvestmentPrinciple updatePrinciple(UpdatePrincipleCommand command);

    void deletePrinciple(Long principleId, Long userId);

    void reorderPrinciples(ReorderPrinciplesCommand command);
}
