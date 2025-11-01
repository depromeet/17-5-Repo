package com.ogd.stockdiary.domain.retrospection.port.in;

public interface DeleteMemoUseCase {

    void deleteMemo(Long retrospectionId, Long memoId, Long userId);
}
