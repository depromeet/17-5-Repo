package com.ogd.stockdiary.domain.investmentprinciple.usecase;

import java.util.List;

import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessResult;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.ReorderPrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;

public interface InvestmentPrincipleUseCase {

    List<InvestmentPrinciple> getUserPrinciples(Long userId);

    InvestmentPrinciple createPrinciple(CreatePrincipleCommand command);

    List<InvestmentPrinciple> createMultiplePrinciples(CreateMultiplePrinciplesCommand command);

    InvestmentPrinciple updatePrinciple(UpdatePrincipleCommand command);

    void deletePrinciple(Long principleId, Long userId);

    BatchProcessResult batchProcessPrinciples(BatchProcessCommand command);

    void reorderPrinciples(ReorderPrinciplesCommand command);
}
