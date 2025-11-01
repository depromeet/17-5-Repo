package com.ogd.stockdiary.application.retrospection.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RetrospectionRepositoryImpl implements RetrospectionRepository {

    private final JpaRetrospectionRepository jpaRetrospectionRepository;

    @Override
    public Retrospection save(Retrospection retrospection) {
        return jpaRetrospectionRepository.save(retrospection);
    }

    @Override
    public Retrospection getById(Long id) {
        return jpaRetrospectionRepository
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException(CodeEnum.FRS_003, "회고를 찾을 수 없습니다: " + id));
    }

    @Override
    public Retrospection findByIdAndUserId(Long id, Long userId) {
        return jpaRetrospectionRepository
            .findByIdAndUserId(id, userId)
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "유저(" + userId + ")에 해당하는 회고(" + id + ")를 찾을 수 없습니다: "));
    }

    @Override
    public List<Retrospection> findAllByUserId(Long userId) {
        return jpaRetrospectionRepository.findAllByUserId(userId);
    }
}
