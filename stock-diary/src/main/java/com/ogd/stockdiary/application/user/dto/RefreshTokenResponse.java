package com.ogd.stockdiary.application.user.dto;

import com.ogd.stockdiary.domain.user.service.TokenRefreshResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;

    public static RefreshTokenResponse from(TokenRefreshResult result) {
        return new RefreshTokenResponse(result.getAccessToken(), result.getRefreshToken());
    }
}
