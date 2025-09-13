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
public class TokenResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("access_token_token_expired")
  private String accessTokenExpired;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("expires_in")
  private Long expiresIn;
}
