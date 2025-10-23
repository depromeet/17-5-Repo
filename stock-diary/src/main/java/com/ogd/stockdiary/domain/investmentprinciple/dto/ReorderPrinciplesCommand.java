package com.ogd.stockdiary.domain.investmentprinciple.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReorderPrinciplesCommand {

    private Long userId;
    private List<PrincipleOrder> principleOrders;

    @Getter
    @AllArgsConstructor
    public static class PrincipleOrder {
        private Long principleId;
        private Integer displayOrder;
    }
}
