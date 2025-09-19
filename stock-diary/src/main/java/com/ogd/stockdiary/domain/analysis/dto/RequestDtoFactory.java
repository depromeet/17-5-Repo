package com.ogd.stockdiary.domain.analysis.dto;

import com.ogd.stockdiary.domain.analysis.port.PromptLoader;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RequestDtoFactory {
  private final PromptLoader promptLoader;

  public RequestDtoFactory(PromptLoader promptLoader) {
    this.promptLoader = promptLoader;
  }

  public RequestDto createRequestDto(String market, String symbol, LocalDateTime time) {
    String prompt = promptLoader.getPrompt();

    RequestDto.Message systemMessage = new RequestDto.Message(RequestDto.Role.system, prompt);

    RequestDto.Message userMessage =
        new RequestDto.Message(
            RequestDto.Role.user,
            String.format("오늘은 %s, 마켓은 %s, 심볼은 %s입니다.", time, market, symbol));

    List<RequestDto.Message> messages = List.of(systemMessage, userMessage);

    Integer maxTokens = 256;

    return new RequestDto(messages, maxTokens);
  }
}
