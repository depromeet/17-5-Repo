package com.dpm05.user.oauth.client;

import com.dpm05.user.domain.OAuthProvider;
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