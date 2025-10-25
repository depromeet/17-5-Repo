package com.ogd.stockdiary.domain.principlecheck.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "투자원칙 준수 상태")
public enum PrincipleCheckStatus {
    KEPT("지켰어요"),

    NEUTRAL("보통이에요"),

    NOT_KEPT("안지켰어요");

    private final String description;
}
