package com.ogd.stockdiary.domain.analysis.dto;

import java.util.List;

public record RequestDto(List<Message> messages, Integer maxTokens) {

  public record Message(Role role, String content) {}

  public enum Role {
    system,
    user,
    assistant
  }
}
