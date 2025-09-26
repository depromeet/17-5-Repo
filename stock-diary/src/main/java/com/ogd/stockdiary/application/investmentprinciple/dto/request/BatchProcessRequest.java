package com.ogd.stockdiary.application.investmentprinciple.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투자원칙 일괄 처리 요청 정보")
public class BatchProcessRequest {

    @Schema(
            description = "생성할 투자원칙 목록",
            example = "[\"손절매는 반드시 10% 이내에서\", \"분산투자로 위험 관리\"]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> createPrinciples;

    @Schema(description = "수정할 투자원칙 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UpdateRequest> updatePrinciples;

    @Schema(
            description = "삭제할 투자원칙 ID 목록",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Long> deletePrincipleIds;

    @Getter
    @NoArgsConstructor
    @Schema(description = "수정할 투자원칙 정보")
    public static class UpdateRequest {
        @Schema(description = "투자원칙 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long principleId;

        @Schema(
                description = "수정할 투자원칙 내용",
                example = "손절매는 반드시 5% 이내에서",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String principle;
    }
}
