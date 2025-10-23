package com.ogd.stockdiary.domain.principlegroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePrincipleGroupCommand {

    private Long groupId;
    private Long userId;
    private String groupName;
}
