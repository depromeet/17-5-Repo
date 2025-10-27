package com.ogd.stockdiary.domain.principlegroup.dto;

import java.util.List;

import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePrincipleGroupCommand {

    private Long userId;
    private String groupName;
    private Integer displayOrder;
    private PrincipleType principleType;
    private List<String> principles; // optional
}
