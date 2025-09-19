package com.ogd.stockdiary.domain.analysis.application;

import com.ogd.stockdiary.domain.analysis.dto.WebClientResDto;
import java.awt.*;
import java.time.LocalDateTime;
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
  //    @GetMapping(value = "/{modelName}")
  public Flux<WebClientResDto> analyze(
      @PathVariable String modelName,
      @RequestParam String market,
      @RequestParam String symbol,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
    return analysisService.analyze(market, symbol, time, modelName);
  }
}
