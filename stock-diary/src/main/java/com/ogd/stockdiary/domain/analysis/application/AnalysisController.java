package com.ogd.stockdiary.domain.analysis.application;

import java.time.LocalDateTime;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.analysis.dto.AnalysisResponse;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping(value = "v1")
    public ResponseEntity<HttpApiResponse<AnalysisResponse>> analyze(
            @RequestParam String market,
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        ChatResponse chatResponse = analysisService.analyze(market, symbol, time);

        String text = chatResponse.getResult().getOutput().getText();

        AnalysisResponse analysisResponse = new AnalysisResponse(text);

        return ResponseEntity.status(HttpStatus.OK).body(HttpApiResponse.of(analysisResponse));
    }

    @GetMapping(value = "v2/{modelName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AssistantMessage> analyzeSteamData(
            @PathVariable String modelName,
            @RequestParam String market,
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        Flux<ChatResponse> chatResponse =
                analysisService.analyzeStreamData(modelName, market, symbol, time);

        Flux<AssistantMessage> messageFlux =
                chatResponse
                        .map(response -> response.getResult())
                        .map(generation -> generation.getOutput());

        return messageFlux;
    }
}
