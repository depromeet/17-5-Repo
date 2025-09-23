package com.ogd.stockdiary.domain.investmentprinciple.port.out;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;

public interface InvestmentPrincipleRepository {

    List<InvestmentPrinciple> findByUserId(Long userId);

    InvestmentPrinciple save(InvestmentPrinciple investmentPrinciple);

    List<InvestmentPrinciple> saveAll(List<InvestmentPrinciple> investmentPrinciples);

    Optional<InvestmentPrinciple> findByIdAndUserId(Long principleId, Long userId);

    void deleteByIdAndUserId(Long principleId, Long userId);

    void deleteAllByIdInAndUserId(List<Long> principleIds, Long userId);
}
