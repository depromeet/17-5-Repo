package com.ogd.stockdiary.domain.principlegroup.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReorderPrincipleGroupsCommand {

    private Long userId;
    private List<GroupOrder> groupOrders;

    @Getter
    @AllArgsConstructor
    public static class GroupOrder {
        private Long groupId;
        private Integer displayOrder;
    }
}
