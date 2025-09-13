package com.ogd.stockdiary.application.stock.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

  @JsonProperty("grant_type")
  private String grantType;

  @JsonProperty("appsecret")
  private String appSecret;

  @JsonProperty("appkey")
  private String appKey;
}
