package com.ogd.stockdiary.application.retrospection.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.retrospection.entity.Memo;
import com.ogd.stockdiary.domain.retrospection.port.out.MemoRepository;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemoRepositoryImpl implements MemoRepository {

    private final JpaMemoRepository jpaMemoRepository;

    @Override
    public Memo save(Memo memo) {
        return jpaMemoRepository.save(memo);
    }

    @Override
    public Memo getById(Long id) {
        return jpaMemoRepository
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException(CodeEnum.FRS_003, "메모를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<Memo> findByRetrospectionIdOrderByIdDesc(Long retrospectionId) {
        return jpaMemoRepository.findByRetrospectionIdOrderByIdDesc(retrospectionId);
    }

    @Override
    public void delete(Memo memo) {
        jpaMemoRepository.delete(memo);
    }
}
