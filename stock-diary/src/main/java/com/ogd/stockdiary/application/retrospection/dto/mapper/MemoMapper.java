package com.ogd.stockdiary.application.retrospection.dto.mapper;

import com.ogd.stockdiary.application.retrospection.dto.response.CreateMemoResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.MemoResponse;
import com.ogd.stockdiary.domain.retrospection.entity.Memo;

public class MemoMapper {

    public static CreateMemoResponse toCreateResponse(Memo memo) {
        return new CreateMemoResponse(
            memo.getId(),
            memo.getContent(),
            memo.getUserId(),
            memo.getCreatedAt(),
            memo.getUpdatedAt());
    }

    public static MemoResponse toResponse(Memo memo) {
        return new MemoResponse(
            memo.getId(),
            memo.getContent());
    }
}
