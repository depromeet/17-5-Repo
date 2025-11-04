package com.ogd.stockdiary.application.retrospection.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;
import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "회고 조회 응답 정보")
public class GetRetrospectionResponse {

    @Schema(description = "회고 ID", example = "15")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "종목 심볼", example = "005930")
    private String symbol;

    @Schema(description = "시장 정보", example = "NASDAQ")
    private String market;

    @Schema(description = "주문 타입", example = "SELL")
    private OrderType orderType;

    @Schema(description = "거래 가격", example = "10000")
    private BigDecimal price;

    @Schema(description = "통화", example = "KRW")
    private Currency currency;

    @Schema(description = "거래량", example = "10")
    private Integer volume;

    @Schema(description = "주문 날짜", example = "2025-09-13")
    private LocalDate orderDate;

    @Schema(description = "수익률", example = "-15.67")
    private Double returnRate;

    @Schema(description = "투자 원칙 목록")
    private List<PrincipleCheckResponse> principleChecks;

    @Schema(description = "메모 목록")
    private List<MemoResponse> memos;

    @Schema(description = "생성일시", example = "2025-09-14T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-09-14T10:00:00")
    private LocalDateTime updatedAt;

    @AllArgsConstructor
    @Getter
    @Schema(description = "투자 원칙 체크 정보")
    public static class PrincipleCheckResponse {

        @Schema(description = "투자 원칙 ID", example = "1")
        private Long principleId;

        @Schema(description = "투자 원칙 내용", example = "손절 라인을 미리 정하고 지킨다")
        private String principle;

        @Schema(description = "투자 원칙 준수 상태", example = "KEPT")
        private PrincipleCheckStatus status;

        @Schema(description = "투자 이유", example = "기술적 분석 결과 상승 추세가 확인되어 매수했습니다.")
        private String reason;

        @Schema(description = "이미지 다운로드 URL 목록", example = "[\"https://example.com/image1.png\", \"https://example.com/image2.png\"]")
        private List<String> imageUrls;

        @Schema(description = "링크 목록", example = "[\"https://example.com/article1\", \"https://example.com/article2\"]")
        private List<String> links;
    }
}
