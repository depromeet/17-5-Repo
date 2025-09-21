package com.ogd.stockdiary.application.principlecheck.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;
import com.ogd.stockdiary.domain.principlecheck.port.out.PrincipleCheckRepository;

@Component
@RequiredArgsConstructor
public class PrincipleCheckRepositoryImpl implements PrincipleCheckRepository {

  private final JpaPrincipleCheckRepository jpaPrincipleCheckRepository;

  @Override
  public PrincipleCheck save(PrincipleCheck principleCheck) {
    return jpaPrincipleCheckRepository.save(principleCheck);
  }

  @Override
  public List<PrincipleCheck> saveAll(List<PrincipleCheck> principleChecks) {
    return jpaPrincipleCheckRepository.saveAll(principleChecks);
  }

  @Override
  public List<PrincipleCheck> findByRetrospectionId(Long retrospectionId) {
    return jpaPrincipleCheckRepository.findByRetrospectionId(retrospectionId);
  }

  @Override
  public void deleteByRetrospectionId(Long retrospectionId) {
    jpaPrincipleCheckRepository.deleteByRetrospectionId(retrospectionId);
  }
}
