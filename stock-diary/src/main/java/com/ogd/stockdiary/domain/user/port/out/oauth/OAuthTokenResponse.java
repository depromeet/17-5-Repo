package com.ogd.stockdiary.domain.user.port.out.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenResponse {
  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private Long expiresIn;

  @JsonProperty("id_token")
  private String idToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("token_type")
  private String tokenType;
}
