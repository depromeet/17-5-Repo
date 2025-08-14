package com.dpm05.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OIDCPublicKeyList {
    @JsonProperty("keys")
    private List<OIDCPublicKey> keys;
}