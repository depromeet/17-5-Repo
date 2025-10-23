package com.ogd.stockdiary.application.investmentprinciple.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투자원칙 순서 변경 요청 정보", example = """
    {
      "principleOrders": [
        {"principleId": 1, "displayOrder": 0},
        {"principleId": 2, "displayOrder": 1},
        {"principleId": 3, "displayOrder": 2}
      ]
    }
    """)
public class ReorderPrinciplesRequest {

    @Schema(description = "순서를 변경할 원칙 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "순서를 변경할 원칙 목록은 필수입니다")
    @Valid
    private List<PrincipleOrder> principleOrders;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "원칙 순서 정보")
    public static class PrincipleOrder {

        @Schema(description = "원칙 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "원칙 ID는 필수입니다")
        private Long principleId;

        @Schema(description = "표시 순서", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "표시 순서는 필수입니다")
        private Integer displayOrder;
    }
}
