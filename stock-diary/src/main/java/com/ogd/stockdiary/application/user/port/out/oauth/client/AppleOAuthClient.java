package com.ogd.stockdiary.application.user.port.out.oauth.client;

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.user.port.out.oauth.OAuthTokenResponse;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClient;
import com.ogd.stockdiary.exception.ApplicationException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public OAuthTokenResponse getToken(String authCode, String redirectUri) {
        // redirectUri가 null이면 application.yml의 설정 사용
        String effectiveRedirectUri = redirectUri != null ? redirectUri : appleProperties.getRedirectUri();

        // redirectUri를 보고 iOS인지 웹인지 판단
        String effectiveClientId = determineClientId(effectiveRedirectUri);

        String clientSecret = generateClientSecret(effectiveClientId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", effectiveClientId);
        params.add("client_secret", clientSecret);
        params.add("code", authCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", effectiveRedirectUri);

        log.info("Apple OAuth token request - clientId: {}, redirectUri: {}", effectiveClientId, effectiveRedirectUri);

        try {
            return restClient
                .post()
                .uri(APPLE_AUTH_URL + TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(OAuthTokenResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Apple OAuth token exchange failed: {}", e.getResponseBodyAsString());
            Map<String, Object> errorData = parseOAuthError(e, "APPLE");
            throw new ApplicationException(
                CodeEnum.AUTH_001,
                "Apple OAuth 토큰 교환 실패",
                errorData);
        }
    }

    /**
     * redirectUri를 보고 어떤 client_id를 사용할지 결정
     * - HTTPS URL이면 웹 → Service ID (clientId)
     * - Bundle ID 형태이면 iOS → App Bundle ID (appId)
     */
    private String determineClientId(String redirectUri) {
        if (redirectUri != null && redirectUri.startsWith("http")) {
            // 웹: Service ID 사용
            return appleProperties.getClientId();
        } else {
            // iOS SDK: App Bundle ID 사용
            return appleProperties.getAppId();
        }
    }

    private Map<String, Object> parseOAuthError(HttpClientErrorException e, String provider) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("provider", provider);
        errorData.put("httpStatus", e.getStatusCode().value());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> errorResponse = objectMapper.readValue(
                e.getResponseBodyAsString(),
                Map.class);
            errorData.putAll(errorResponse);
        } catch (Exception parseException) {
            errorData.put("rawError", e.getResponseBodyAsString());
        }

        return errorData;
    }

    @Override
    public OAuthTokenResponse getTokenFromIdToken(String idToken) {
        // iOS SDK에서 받은 idToken을 그대로 반환
        // idToken만 있으면 사용자 정보 추출 가능
        return new OAuthTokenResponse(null, null, idToken, null, null);
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
        // unlink는 일반적으로 iOS에서 사용되므로 app-id 사용
        String clientId = appleProperties.getAppId();
        String clientSecret = generateClientSecret(clientId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
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

    private String generateClientSecret(String clientId) {
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
                .setAudience(AppleProperties.APPLE_AUD)
                .setSubject(clientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
        } catch (Exception e) {
            log.error("Failed to generate Apple client secret", e);
            throw new RuntimeException("Failed to generate Apple client secret", e);
        }
    }

    private PrivateKey getPrivateKey() throws IOException {
        try {
            // 우선순위 1: .p8 파일이 있으면 직접 읽기 (로컬 테스트용)
            if (appleProperties.getKeyFilePath() != null && !appleProperties.getKeyFilePath().isEmpty()) {
                return readPrivateKeyFromFile(appleProperties.getKeyFilePath());
            }

            // 우선순위 2: 환경변수에서 읽기 (배포 환경용)
            String privateKeyPEM = appleProperties.getPrivateKey().replace("\\n", "\n");
            try (PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM))) {
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                return converter.getPrivateKey(privateKeyInfo);
            }
        } catch (Exception e) {
            log.error("Failed to load Apple private key", e);
            throw new IOException("Failed to load Apple private key", e);
        }
    }

    private PrivateKey readPrivateKeyFromFile(String keyFilePath) throws IOException {
        org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource(keyFilePath);

        try (java.io.InputStream is = resource.getInputStream();
            java.io.InputStreamReader isr = new java.io.InputStreamReader(is);
            PEMParser pemParser = new PEMParser(isr)) {

            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            if (privateKeyInfo == null) {
                throw new IOException("Failed to parse private key from file: " + keyFilePath);
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPrivateKey(privateKeyInfo);
        }
    }
}
