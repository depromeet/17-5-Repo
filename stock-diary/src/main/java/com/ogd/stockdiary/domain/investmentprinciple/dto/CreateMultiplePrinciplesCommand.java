package com.ogd.stockdiary.domain.investmentprinciple.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateMultiplePrinciplesCommand {

    private final Long userId;
    private final Long groupId;
    private final List<PrincipleItem> principles;

    @Getter
    @RequiredArgsConstructor
    public static class PrincipleItem {
        private final String principle;
        private final String description;
    }
}
