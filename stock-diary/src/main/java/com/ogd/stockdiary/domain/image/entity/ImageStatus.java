package com.ogd.stockdiary.domain.image.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "이미지 상태")
public enum ImageStatus {
    T("임시저장"),

    C("저장완료"),

    D("삭제");

    private final String description;
}
