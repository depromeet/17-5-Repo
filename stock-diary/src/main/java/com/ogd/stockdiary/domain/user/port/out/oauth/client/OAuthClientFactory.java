package com.ogd.stockdiary.domain.user.port.out.oauth.client;

import com.ogd.stockdiary.application.user.port.out.oauth.client.AppleOAuthClient;
import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthClientFactory {
    
    private final AppleOAuthClient appleOAuthClient;
    
    public OAuthClient getClient(OAuthProvider provider) {
        return switch (provider) {
            case APPLE -> appleOAuthClient;
            case GOOGLE, KAKAO -> throw new UnsupportedOperationException(provider + " not implemented yet");
        };
    }
}