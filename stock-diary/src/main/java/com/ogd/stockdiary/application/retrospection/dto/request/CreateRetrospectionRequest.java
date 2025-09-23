package com.ogd.stockdiary.application.retrospection.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.ogd.stockdiary.application.principlecheck.dto.request.PrincipleCheckRequest;
import com.ogd.stockdiary.domain.retrospection.entity.Currency;
import com.ogd.stockdiary.domain.retrospection.entity.InvestmentEmotion;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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

    @Schema(description = "주문 타입", example = "SELL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 타입은 필수입니다")
    private OrderType orderType;

    @Schema(description = "거래 가격", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 0보다 큰 값이어야 합니다")
    private BigDecimal price;

    @Schema(description = "통화", example = "KRW", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "통화는 필수입니다")
    private Currency currency;

    @Schema(description = "거래량", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "거래량은 필수입니다")
    @Positive(message = "거래량은 0보다 큰 값이어야 합니다")
    private Integer volume;

    @Schema(
            description = "주문 날짜",
            example = "2025-09-13",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 날짜는 필수입니다")
    private LocalDate orderDate;

    @Schema(description = "수익률", example = "-15.67", requiredMode = RequiredMode.NOT_REQUIRED)
    private Double returnRate;

    @Schema(
            description = "회고 내용",
            example = "이번 매도는 시장 상황을 잘 반영한 결정이었다.",
            requiredMode = RequiredMode.NOT_REQUIRED)
    private String content;

    @Schema(description = "투자원칙 체크 목록", requiredMode = RequiredMode.NOT_REQUIRED)
    @Valid
    private List<PrincipleCheckRequest> principleChecks;

    @Schema(description = "투자 감정", example = "CONFIDENCE", requiredMode = RequiredMode.NOT_REQUIRED)
    private InvestmentEmotion emotion;
}
