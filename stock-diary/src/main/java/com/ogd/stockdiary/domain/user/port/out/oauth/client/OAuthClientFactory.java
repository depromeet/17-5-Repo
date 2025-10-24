package com.ogd.stockdiary.domain.user.port.out.oauth.client;

import org.springframework.stereotype.Component;

import com.ogd.stockdiary.application.user.port.out.oauth.client.AppleOAuthClient;
import com.ogd.stockdiary.application.user.port.out.oauth.client.KakaoOAuthClient;
import com.ogd.stockdiary.domain.user.entity.OAuthProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthClientFactory {

    private final AppleOAuthClient appleOAuthClient;
    private final KakaoOAuthClient kakaoOAuthClient;

    public OAuthClient getClient(OAuthProvider provider) {
        return switch (provider) {
            case APPLE -> appleOAuthClient;
            case KAKAO -> kakaoOAuthClient;
            case GOOGLE ->
                throw new UnsupportedOperationException(provider + " not implemented yet");
        };
    }
}
