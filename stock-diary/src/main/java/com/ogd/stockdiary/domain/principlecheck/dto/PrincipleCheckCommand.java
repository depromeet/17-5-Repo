package com.ogd.stockdiary.domain.principlecheck.dto;

import java.util.List;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrincipleCheckCommand {

    private final Long principleId;
    private final PrincipleCheckStatus status;
    private final String reason;
    private final List<Long> imageIds;
    private final List<String> links;
}
