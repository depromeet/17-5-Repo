package com.ogd.stockdiary.domain.analysis.dto;

public record WebClientResDto(Message message) {
  public record Message(Role role, String content) {}

  public enum Role {
    system,
    user,
    assistant
  }
}
