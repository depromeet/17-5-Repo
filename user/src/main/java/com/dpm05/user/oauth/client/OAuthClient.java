package com.dpm05.user.oauth.client;

import com.dpm05.user.oauth.OAuthTokenResponse;
import com.dpm05.user.oauth.OIDCPublicKeyList;

public interface OAuthClient {
    OAuthTokenResponse getToken(String authCode);
    OIDCPublicKeyList getPublicKeys();
    void unlink(String identifier);
}