package com.ogd.stockdiary.domain.principlegroup.dto;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePrincipleGroupCommand {

    private Long groupId;
    private Long userId;
    private String groupName;
    private PrincipleType principleType;
}
