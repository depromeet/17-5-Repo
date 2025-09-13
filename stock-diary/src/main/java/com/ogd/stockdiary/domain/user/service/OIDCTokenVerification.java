package com.ogd.stockdiary.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPayload;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKey;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;
import io.jsonwebtoken.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OIDCTokenVerification {

  private final ObjectMapper objectMapper;

  public OIDCPayload verifyIdToken(String idToken, OIDCPublicKeyList oidcPublicKeys) {
    try {
      String[] chunks = idToken.split("\\.");
      String header = new String(Base64.getUrlDecoder().decode(chunks[0]));

      Map<String, Object> headerMap = objectMapper.readValue(header, Map.class);
      String kid = (String) headerMap.get("kid");
      String alg = (String) headerMap.get("alg");

      OIDCPublicKey matchingKey =
          oidcPublicKeys.getKeys().stream()
              .filter(key -> key.getKid().equals(kid))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("No matching public key found"));

      PublicKey publicKey = generateRSAPublicKey(matchingKey.getN(), matchingKey.getE());

      Claims claims =
          Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(idToken).getBody();

      return new OIDCPayload(
          claims.getSubject(),
          claims.get("email", String.class),
          claims.get("picture", String.class),
          claims.get("name", String.class));

    } catch (ExpiredJwtException e) {
      log.error("ID token has expired", e);
      throw new RuntimeException("ID token has expired", e);
    } catch (SignatureException e) {
      log.error("ID token signature verification failed", e);
      throw new RuntimeException("Invalid ID token signature", e);
    } catch (MalformedJwtException e) {
      log.error("ID token is malformed", e);
      throw new RuntimeException("Malformed ID token", e);
    } catch (Exception e) {
      log.error("Failed to verify ID token", e);
      throw new RuntimeException("Failed to verify ID token", e);
    }
  }

  private PublicKey generateRSAPublicKey(String n, String e)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] nBytes = Base64.getUrlDecoder().decode(n);
    byte[] eBytes = Base64.getUrlDecoder().decode(e);

    BigInteger modulus = new BigInteger(1, nBytes);
    BigInteger exponent = new BigInteger(1, eBytes);

    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    return keyFactory.generatePublic(keySpec);
  }
}
