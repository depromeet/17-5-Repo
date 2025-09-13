package com.ogd.stockdiary.application.sample.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "주식 생성 요청 정보")
public class SampleStockCreateRequest {

  @Schema(description = "주식 코드", example = "005930", requiredMode = Schema.RequiredMode.REQUIRED)
  private String stockCode;

  @Schema(description = "주식명", example = "삼성전자", requiredMode = Schema.RequiredMode.REQUIRED)
  private String stockName;

  @Schema(description = "매매가격", example = "75000", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long price;

  @Schema(description = "매매수량", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer quantity;

  @Schema(
      description = "매매타입 (BUY/SELL)",
      example = "BUY",
      allowableValues = {"BUY", "SELL"},
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String tradeType;

  @Schema(
      description = "메모",
      example = "기술적 분석에 따른 매수",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String memo;
}
