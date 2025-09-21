package com.ogd.stockdiary.domain.principlecheck.port.out;

import java.util.List;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;

public interface PrincipleCheckRepository {

  PrincipleCheck save(PrincipleCheck principleCheck);

  List<PrincipleCheck> saveAll(List<PrincipleCheck> principleChecks);

  List<PrincipleCheck> findByRetrospectionId(Long retrospectionId);

  void deleteByRetrospectionId(Long retrospectionId);
}
