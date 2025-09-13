package com.ogd.stockdiary.application.retrospection.dto.response;

import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "회고 생성 응답 정보")
public class CreateRetrospectionResponse {

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

    @Schema(description = "생성일시", example = "2025-09-14T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-09-14T10:00:00")
    private LocalDateTime updatedAt;
}