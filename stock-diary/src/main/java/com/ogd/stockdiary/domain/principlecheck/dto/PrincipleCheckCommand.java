package com.ogd.stockdiary.domain.principlecheck.dto;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrincipleCheckCommand {

    private final Long principleId;
    private final PrincipleCheckStatus status;
}
