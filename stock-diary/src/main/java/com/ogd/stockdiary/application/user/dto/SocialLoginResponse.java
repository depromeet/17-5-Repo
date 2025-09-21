package com.ogd.stockdiary.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.ogd.stockdiary.domain.user.entity.User;

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
    return new SocialLoginResponse(user.getId(), user.getNickname(), user.getEmail(), user.getProfileImageUrl(),
        isNewUser);
  }
}
