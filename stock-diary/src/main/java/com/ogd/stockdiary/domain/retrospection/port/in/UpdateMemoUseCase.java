package com.ogd.stockdiary.domain.retrospection.port.in;

import com.ogd.stockdiary.domain.retrospection.entity.Memo;

public interface UpdateMemoUseCase {

    Memo updateMemo(Long retrospectionId, Long memoId, String content, Long userId);
}
