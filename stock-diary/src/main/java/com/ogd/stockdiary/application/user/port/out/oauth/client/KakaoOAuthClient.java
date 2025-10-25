package com.ogd.stockdiary.application.user.port.out.oauth.client;

import java.util.HashMap;
import java.util.Map;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient implements OAuthClient {

    private final KakaoProperties kakaoProperties;
    private final RestClient restClient = RestClient.create();

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private static final String KAKAO_API_URL = "https://kapi.kakao.com";
    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String JWKS_ENDPOINT = "/.well-known/jwks.json";
    private static final String UNLINK_ENDPOINT = "/v1/user/unlink";

    @Override
    public OAuthTokenResponse getToken(String authCode, String redirectUri) {
        // redirectUri가 null이면 application.yml의 설정 사용
        String effectiveRedirectUri = redirectUri != null ? redirectUri : kakaoProperties.getRedirectUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("redirect_uri", effectiveRedirectUri);
        params.add("code", authCode);

        if (kakaoProperties.getClientSecret() != null && !kakaoProperties.getClientSecret().isEmpty()) {
            params.add("client_secret", kakaoProperties.getClientSecret());
        }

        try {
            return restClient
                .post()
                .uri(KAKAO_AUTH_URL + TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(OAuthTokenResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Kakao OAuth token exchange failed: {}", e.getResponseBodyAsString());
            Map<String, Object> errorData = parseOAuthError(e, "KAKAO");
            throw new ApplicationException(
                CodeEnum.AUTH_001,
                "Kakao OAuth 토큰 교환 실패",
                errorData);
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
    @Cacheable(value = "oidcPublicKeys", key = "'kakao'")
    public OIDCPublicKeyList getPublicKeys() {
        return restClient
            .get()
            .uri(KAKAO_AUTH_URL + JWKS_ENDPOINT)
            .retrieve()
            .body(OIDCPublicKeyList.class);
    }

    @Override
    public void unlink(String identifier) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", identifier);

        restClient
            .post()
            .uri(KAKAO_API_URL + UNLINK_ENDPOINT)
            .header("Authorization", "KakaoAK " + kakaoProperties.getAdminKey())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .toBodilessEntity();
    }
}
