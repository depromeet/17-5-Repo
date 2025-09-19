package com.ogd.stockdiary.domain.analysis.port;

import com.ogd.stockdiary.domain.analysis.dto.RequestDto;
import com.ogd.stockdiary.domain.analysis.dto.ResponseDto;
import com.ogd.stockdiary.domain.analysis.dto.WebClientResDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Qualifier("analysis-service") public class AnalysisWebClient {

  private final WebClient webClient;

  public AnalysisWebClient(WebClient.Builder webClientBuilder) {
    this.webClient =
        webClientBuilder
            .baseUrl("https://clovastudio.stream.ntruss.com")
            .filter(
                (clientRequest, nextFilter) -> {
                  ClientRequest filteredRequest =
                      ClientRequest.from(clientRequest)
                          .header("Authorization", "Bearer nv-18cb916bc90a40fdbf2fa23111d76a26sqRG")
                              .header("Content-Type", "application/json")
                          .header("Accept", "text/event-stream")
                          .build();
                  return nextFilter.exchange(filteredRequest);
                })
            .build();
  }

  public Flux<WebClientResDto> analysisMarket(String modelName, RequestDto requestDto) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/v1/chat-completions/{modelName}").build(modelName))
        .accept(MediaType.TEXT_EVENT_STREAM)
        .bodyValue(requestDto)
        .retrieve()
        .bodyToFlux(WebClientResDto.class)
            .onErrorResume(e -> Flux.just(
                    new WebClientResDto(
                            new WebClientResDto.Message(
                                    WebClientResDto.Role.assistant,
                                    "Error: " + e.getMessage()
                            ))));
  }
}
