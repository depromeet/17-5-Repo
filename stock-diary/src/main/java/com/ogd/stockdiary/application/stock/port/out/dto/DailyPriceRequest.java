package com.ogd.stockdiary.application.stock.port.out.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceRequest {

  private String market;
  private String symbol;
  private String period;
  private String baseDate;
  private String modifiedPrice;

  public DailyPriceRequest(String market, String symbol, String period) {
    this.market = market;
    this.symbol = symbol;
    this.period = period;
    this.baseDate = "";
    this.modifiedPrice = "1";
  }
}
