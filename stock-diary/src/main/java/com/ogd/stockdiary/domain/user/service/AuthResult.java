package com.ogd.stockdiary.domain.user.service;

import com.ogd.stockdiary.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {
    private final User user;
    private final boolean isNewUser;
}