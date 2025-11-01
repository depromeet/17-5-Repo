package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.domain.retrospection.entity.Memo;

public interface CreateMemoUseCase {

    Memo createMemo(Long retrospectionId, String content, Long userId);
}
