package com.ogd.stockdiary.domain.analysis.application;

import java.time.LocalDateTime;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.analysis.dto.AnalysisResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/analysis")
public class AnalysisController {

  private final AnalysisService analysisService;

  public AnalysisController(AnalysisService analysisService) {
    this.analysisService = analysisService;
  }

  @GetMapping(value = "/{modelName}")
  public ResponseEntity<HttpApiResponse<AnalysisResponse>> analyze(
      @PathVariable String modelName,
      @RequestParam String market,
      @RequestParam String symbol,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

    ChatResponse chatResponse = analysisService.analyze(modelName, market, symbol, time);

    String text = chatResponse.getResult().getOutput().getText();

    AnalysisResponse analysisResponse = new AnalysisResponse(text);

    return ResponseEntity.status(HttpStatus.OK).body(HttpApiResponse.of(analysisResponse));
  }
}
