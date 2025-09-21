package com.ogd.stockdiary.domain.user.port.out.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OIDCPublicKey {
  @JsonProperty("kty")
  private String kty;

  @JsonProperty("kid")
  private String kid;

  @JsonProperty("use")
  private String use;

  @JsonProperty("alg")
  private String alg;

  @JsonProperty("n")
  private String n;

  @JsonProperty("e")
  private String e;
}
