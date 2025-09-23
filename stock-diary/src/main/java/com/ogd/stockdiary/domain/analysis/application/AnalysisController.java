package com.ogd.stockdiary.domain.analysis.application;

import java.time.LocalDateTime;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/analysis")
public class AnalysisController {

  private final AnalysisService analysisService;

  public AnalysisController(AnalysisService analysisService) {
    this.analysisService = analysisService;
  }

  @GetMapping(value = "/{modelName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<AssistantMessage> analyze(
      @PathVariable String modelName,
      @RequestParam String market,
      @RequestParam String symbol,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

    Flux<ChatResponse> chatResponseFlux = analysisService.analyze(modelName, market, symbol, time);

    return chatResponseFlux
        .map(chatResponse -> chatResponse.getResult())
        .map(generation -> generation.getOutput());
  }
}
