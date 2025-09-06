package com.ogd.stockdiary.application.user.port.out.oauth.client;

import com.ogd.stockdiary.domain.user.port.out.oauth.OAuthTokenResponse;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthClient implements OAuthClient {

  private final AppleProperties appleProperties;
  private final RestClient restClient = RestClient.create();

  private static final String APPLE_AUTH_URL = "https://appleid.apple.com";
  private static final String TOKEN_ENDPOINT = "/auth/token";
  private static final String REVOKE_ENDPOINT = "/auth/revoke";
  private static final String KEYS_ENDPOINT = "/auth/keys";

  @Override
  public OAuthTokenResponse getToken(String authCode) {
    String clientSecret = generateClientSecret();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("client_id", appleProperties.getClientId());
    params.add("client_secret", clientSecret);
    params.add("code", authCode);
    params.add("grant_type", "authorization_code");
    params.add("redirect_uri", appleProperties.getRedirectUri());

    return restClient
        .post()
        .uri(APPLE_AUTH_URL + TOKEN_ENDPOINT)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(params)
        .retrieve()
        .body(OAuthTokenResponse.class);
  }

  @Override
  @Cacheable(value = "oidcPublicKeys", key = "'apple'")
  public OIDCPublicKeyList getPublicKeys() {
    return restClient
        .get()
        .uri(APPLE_AUTH_URL + KEYS_ENDPOINT)
        .retrieve()
        .body(OIDCPublicKeyList.class);
  }

  @Override
  public void unlink(String identifier) {
    String clientSecret = generateClientSecret();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("client_id", appleProperties.getClientId());
    params.add("client_secret", clientSecret);
    params.add("token", identifier);
    params.add("token_type_hint", "refresh_token");

    restClient
        .post()
        .uri(APPLE_AUTH_URL + REVOKE_ENDPOINT)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(params)
        .retrieve()
        .toBodilessEntity();
  }

  private String generateClientSecret() {
    try {
      LocalDateTime now = LocalDateTime.now();
      Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
      Date expiration = Date.from(now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());

      return Jwts.builder()
          .setHeaderParam("kid", appleProperties.getKeyId())
          .setHeaderParam("alg", "ES256")
          .setIssuer(appleProperties.getTeamId())
          .setIssuedAt(issuedAt)
          .setExpiration(expiration)
          .setAudience(appleProperties.getAud())
          .setSubject(appleProperties.getClientId())
          .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
          .compact();
    } catch (Exception e) {
      log.error("Failed to generate Apple client secret", e);
      throw new RuntimeException("Failed to generate Apple client secret", e);
    }
  }

  private PrivateKey getPrivateKey() throws IOException {
    String privateKeyPEM = new String(Base64.getDecoder().decode(appleProperties.getPrivateKey()));

    try (PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM))) {
      PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
      return converter.getPrivateKey(privateKeyInfo);
    }
  }
}
