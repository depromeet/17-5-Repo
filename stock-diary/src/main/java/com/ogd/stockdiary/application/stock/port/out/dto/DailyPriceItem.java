package com.ogd.stockdiary.application.stock.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyPriceItem {

  @JsonProperty("xymd")
  private String date;

  @JsonProperty("clos")
  private String closePrice;

  @JsonProperty("sign")
  private String sign;

  @JsonProperty("diff")
  private String difference;

  @JsonProperty("rate")
  private String changeRate;

  @JsonProperty("open")
  private String openPrice;

  @JsonProperty("high")
  private String highPrice;

  @JsonProperty("low")
  private String lowPrice;

  @JsonProperty("tvol")
  private String tradingVolume;

  @JsonProperty("tamt")
  private String tradingAmount;

  @JsonProperty("pbid")
  private String priceBid;

  @JsonProperty("vbid")
  private String volumeBid;

  @JsonProperty("pask")
  private String priceAsk;

  @JsonProperty("vask")
  private String volumeAsk;
}
