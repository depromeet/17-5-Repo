package com.ogd.stockdiary.application.investmentprinciple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;

@Repository
@RequiredArgsConstructor
public class InvestmentPrincipleRepositoryImpl implements InvestmentPrincipleRepository {

  private final JpaInvestmentPrincipleRepository jpaInvestmentPrincipleRepository;

  @Override
  public List<InvestmentPrinciple> findByUserId(Long userId) {
    return jpaInvestmentPrincipleRepository.findByUserId(userId);
  }

  @Override
  public InvestmentPrinciple save(InvestmentPrinciple investmentPrinciple) {
    return jpaInvestmentPrincipleRepository.save(investmentPrinciple);
  }

  @Override
  public List<InvestmentPrinciple> saveAll(List<InvestmentPrinciple> investmentPrinciples) {
    return jpaInvestmentPrincipleRepository.saveAll(investmentPrinciples);
  }

  @Override
  public Optional<InvestmentPrinciple> findByIdAndUserId(Long principleId, Long userId) {
    return jpaInvestmentPrincipleRepository.findByIdAndUserId(principleId, userId);
  }

  @Override
  public void deleteByIdAndUserId(Long principleId, Long userId) {
    jpaInvestmentPrincipleRepository.deleteByIdAndUserId(principleId, userId);
  }

  @Override
  public void deleteAllByIdInAndUserId(List<Long> principleIds, Long userId) {
    jpaInvestmentPrincipleRepository.deleteAllByIdInAndUserId(principleIds, userId);
  }
}
