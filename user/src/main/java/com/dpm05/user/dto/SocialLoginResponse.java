package com.dpm05.user.dto;

import com.dpm05.user.domain.User;
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
    
    public static SocialLoginResponse from(User user, boolean isNewUser) {
        return new SocialLoginResponse(
            user.getId(),
            user.getNickname(),
            user.getEmail(),
            user.getProfileImageUrl(),
            isNewUser
        );
    }
}