package com.ogd.stockdiary.application.retrospection.dto.request;

import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "회고 생성 요청 정보")
public class CreateRetrospectionRequest {

    @Schema(description = "종목 심볼", example = "005930", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "종목 심볼은 필수입니다")
    private String symbol;

    @Schema(description = "시장 정보", example = "NASDAQ", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "시장 정보는 필수입니다")
    private String market;

    @Schema(description = "주문 타입", example = "SELL", allowableValues = {"BUY", "SELL"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 타입은 필수입니다")
    private OrderType orderType;

    @Schema(description = "거래 가격", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 0보다 큰 값이어야 합니다")
    private BigDecimal price;

    @Schema(description = "통화", example = "KRW", allowableValues = {"KRW", "USD", "EUR", "JPY"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "통화는 필수입니다")
    private Currency currency;

    @Schema(description = "거래량", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "거래량은 필수입니다")
    @Positive(message = "거래량은 0보다 큰 값이어야 합니다")
    private Integer volume;

    @Schema(description = "주문 날짜", example = "2025-09-13", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 날짜는 필수입니다")
    private LocalDate orderDate;

    @Schema(description = "수익률", example = "-15.67", requiredMode = RequiredMode.NOT_REQUIRED)
    private Double returnRate;
}