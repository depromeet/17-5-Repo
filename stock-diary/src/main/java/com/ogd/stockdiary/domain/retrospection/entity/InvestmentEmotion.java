package com.ogd.stockdiary.domain.retrospection.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvestmentEmotion {
    ANXIETY(1, "불안"), IMPULSE(2, "충동"), MINDLESSNESS(3, "무념무상"), CONFIDENCE(4, "자신감"), CONVICTION(5, "확신");

    private final int value;
    private final String description;
}
