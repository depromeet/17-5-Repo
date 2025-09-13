package com.ogd.stockdiary.application.retrospection.service;

import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrospectionService implements CreateRetrospectionUseCase {

  private final RetrospectionRepository retrospectionRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public Retrospection createRetrospection(CreateRetrospectionCommand command) {
    // 사용자 조회
    User user =
        userRepository
            .findById(command.getUserId())
            .orElseThrow(
                () ->
                    new ApplicationException(
                        CodeEnum.FRS_003, "사용자를 찾을 수 없습니다: " + command.getUserId()));

    // 엔티티 생성
    Retrospection retrospection = RetrospectionMapper.toEntity(command, user);

    // 저장 및 반환
    return retrospectionRepository.save(retrospection);
  }
}
