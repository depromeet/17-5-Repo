package com.ogd.stockdiary.domain.retrospection.port.in;

public interface UpdateMemoUseCase {

    void updateMemo(Long retrospectionId, Long memoId, String content, Long userId);
}
