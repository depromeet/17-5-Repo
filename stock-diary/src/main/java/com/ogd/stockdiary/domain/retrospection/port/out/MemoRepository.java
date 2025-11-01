package com.ogd.stockdiary.domain.retrospection.port.out;

import java.util.List;

import com.ogd.stockdiary.domain.retrospection.entity.Memo;

public interface MemoRepository {

    Memo save(Memo memo);

    Memo getById(Long id);

    List<Memo> findByRetrospectionIdOrderByIdDesc(Long retrospectionId);

    void delete(Memo memo);
}
