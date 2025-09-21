package com.ogd.stockdiary.application.investmentprinciple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.investmentprinciple.InvestmentPrinciple;

@Repository
public interface JpaInvestmentPrincipleRepository extends JpaRepository<InvestmentPrinciple, Long> {

  List<InvestmentPrinciple> findByUserId(Long userId);

  Optional<InvestmentPrinciple> findByIdAndUserId(Long id, Long userId);

  @Modifying
  @Query("DELETE FROM InvestmentPrinciple ip WHERE ip.id = :principleId AND ip.user.id = :userId")
  void deleteByIdAndUserId(@Param("principleId") Long principleId, @Param("userId") Long userId);

  @Modifying
  @Query("DELETE FROM InvestmentPrinciple ip WHERE ip.id IN :principleIds AND ip.user.id = :userId")
  void deleteAllByIdInAndUserId(@Param("principleIds") List<Long> principleIds, @Param("userId") Long userId);
}
