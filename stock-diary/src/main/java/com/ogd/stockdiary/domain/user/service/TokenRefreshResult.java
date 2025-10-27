package com.ogd.stockdiary.domain.user.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResult {
    private String accessToken;
    private String refreshToken;
}
