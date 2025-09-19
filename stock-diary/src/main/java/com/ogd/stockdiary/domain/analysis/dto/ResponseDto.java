package com.ogd.stockdiary.domain.analysis.dto;

public record ResponseDto(Result result) {
  public record Result(Message message) {}

  public record Message(String content) {}
}
