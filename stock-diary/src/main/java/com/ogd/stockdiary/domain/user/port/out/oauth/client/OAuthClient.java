package com.ogd.stockdiary.domain.user.port.out.oauth.client;

import com.ogd.stockdiary.domain.user.port.out.oauth.OAuthTokenResponse;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;

public interface OAuthClient {
    OAuthTokenResponse getToken(String authCode);
    OIDCPublicKeyList getPublicKeys();
    void unlink(String identifier);
}