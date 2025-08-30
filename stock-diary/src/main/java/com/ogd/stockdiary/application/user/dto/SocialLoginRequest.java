package com.ogd.stockdiary.application.user.dto;

import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    private OAuthProvider provider;
    private String authCode;
    private String email;      // Apple의 경우 클라이언트에서 제공
    private String nickname;   // Apple의 경우 클라이언트에서 제공
}