package com.ogd.stockdiary.application.user.dto;

import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.domain.user.service.AuthResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    private Long userId;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private boolean isNewUser;
    private String accessToken;
    private String refreshToken;

    public static SocialLoginResponse from(AuthResult authResult) {
        User user = authResult.getUser();
        return new SocialLoginResponse(
            user.getId(),
            user.getNickname(),
            user.getEmail(),
            user.getProfileImageUrl(),
            authResult.isNewUser(),
            authResult.getAccessToken(),
            authResult.getRefreshToken());
    }
}
