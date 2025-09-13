package com.ogd.stockdiary.domain.user.port.out.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OIDCPublicKeyList {
  @JsonProperty("keys")
  private List<OIDCPublicKey> keys;
}
