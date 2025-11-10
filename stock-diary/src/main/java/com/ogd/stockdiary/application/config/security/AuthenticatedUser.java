package com.ogd.stockdiary.application.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final Long userId;
}
