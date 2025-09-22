package com.ogd.stockdiary.domain.analysis.dto;

public record ResponseDto(Choices choices) {
  public record Choices(Delta delta) {}

  public record Delta(String content) {}
}
