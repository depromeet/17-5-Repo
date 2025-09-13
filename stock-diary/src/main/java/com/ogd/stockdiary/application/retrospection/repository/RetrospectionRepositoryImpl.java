package com.ogd.stockdiary.application.retrospection.repository;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;
import com.ogd.stockdiary.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RetrospectionRepositoryImpl implements RetrospectionRepository {

  private final JpaRetrospectionRepository jpaRetrospectionRepository;

  @Override
  public Retrospection save(Retrospection retrospection) {
    return jpaRetrospectionRepository.save(retrospection);
  }

  @Override
  public Retrospection findById(Long id) {
    return jpaRetrospectionRepository
        .findById(id)
        .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003, "회고를 찾을 수 없습니다: " + id));
  }

  @Override
  public List<Retrospection> findByUserId(Long userId) {
    return jpaRetrospectionRepository.findByUserId(userId);
  }
}
