package com.ogd.stockdiary.domain.investmentprinciple.port.out;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

public interface InvestmentPrincipleRepository {

    List<InvestmentPrinciple> findByUserId(Long userId);

    List<InvestmentPrinciple> findByUserIdAndPrincipleType(Long userId, PrincipleType principleType);

    InvestmentPrinciple save(InvestmentPrinciple investmentPrinciple);

    List<InvestmentPrinciple> saveAll(List<InvestmentPrinciple> investmentPrinciples);

    Optional<InvestmentPrinciple> findByIdAndUserId(Long principleId, Long userId);

    void deleteByIdAndUserId(Long principleId, Long userId);

    void deleteAllByIdInAndUserId(List<Long> principleIds, Long userId);

    List<InvestmentPrinciple> findByPrincipleGroupId(Long groupId);

    void deleteByPrincipleGroupId(Long groupId);
}
