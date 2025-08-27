package com.dpm05.user.service;

import com.dpm05.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {
    private final User user;
    private final boolean isNewUser;
}