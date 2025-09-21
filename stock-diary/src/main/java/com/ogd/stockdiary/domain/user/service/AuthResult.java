package com.ogd.stockdiary.domain.user.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.ogd.stockdiary.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class AuthResult {
  private final User user;
  private final boolean isNewUser;
}
