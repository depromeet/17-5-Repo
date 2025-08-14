package com.dpm05.user.oauth.client;

import com.dpm05.user.config.AppleProperties;
import com.dpm05.user.oauth.OAuthTokenResponse;
import com.dpm05.user.oauth.OIDCPublicKeyList;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthClient implements OAuthClient {
    
    private final AppleProperties appleProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String APPLE_AUTH_URL = "https://appleid.apple.com";
    private static final String TOKEN_ENDPOINT = "/auth/token";
    private static final String REVOKE_ENDPOINT = "/auth/revoke";
    private static final String KEYS_ENDPOINT = "/auth/keys";
    
    @Override
    public OAuthTokenResponse getToken(String authCode) {
        String clientSecret = generateClientSecret();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.getClientId());
        params.add("client_secret", clientSecret);
        params.add("code", authCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", appleProperties.getRedirectUri());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<OAuthTokenResponse> response = restTemplate.postForEntity(
            APPLE_AUTH_URL + TOKEN_ENDPOINT, 
            request, 
            OAuthTokenResponse.class
        );
        
        return response.getBody();
    }
    
    @Override
    @Cacheable(value = "oidcPublicKeys", key = "'apple'")
    public OIDCPublicKeyList getPublicKeys() {
        ResponseEntity<OIDCPublicKeyList> response = restTemplate.getForEntity(
            APPLE_AUTH_URL + KEYS_ENDPOINT, 
            OIDCPublicKeyList.class
        );
        
        return response.getBody();
    }
    
    @Override
    public void unlink(String identifier) {
        String clientSecret = generateClientSecret();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.getClientId());
        params.add("client_secret", clientSecret);
        params.add("token", identifier);
        params.add("token_type_hint", "refresh_token");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        restTemplate.postForEntity(APPLE_AUTH_URL + REVOKE_ENDPOINT, request, String.class);
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