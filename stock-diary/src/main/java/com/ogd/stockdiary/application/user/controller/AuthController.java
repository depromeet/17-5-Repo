package com.ogd.stockdiary.application.user.controller;

import com.ogd.stockdiary.application.user.dto.SocialLoginRequest;
import com.ogd.stockdiary.application.user.dto.SocialLoginResponse;
import com.ogd.stockdiary.domain.user.service.AuthResult;
import com.ogd.stockdiary.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/social/login")
  public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request) {
    try {
      AuthResult authResult =
          authService.socialLogin(
              request.getProvider(),
              request.getAuthCode(),
              request.getEmail(),
              request.getNickname());

      SocialLoginResponse response =
          SocialLoginResponse.from(authResult.getUser(), authResult.isNewUser());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Social login failed", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/social/unlink/{userId}")
  public ResponseEntity<Void> unlinkSocialAccount(@PathVariable Long userId) {
    try {
      authService.unlinkSocialAccount(userId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Social account unlink failed for user: {}", userId, e);
      return ResponseEntity.badRequest().build();
    }
  }
}
