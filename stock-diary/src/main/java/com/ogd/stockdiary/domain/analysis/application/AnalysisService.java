package com.ogd.stockdiary.domain.analysis.application;

import com.ogd.stockdiary.domain.analysis.dto.RequestDto;
import com.ogd.stockdiary.domain.analysis.dto.RequestDtoFactory;
import com.ogd.stockdiary.domain.analysis.dto.ResponseDto;
import com.ogd.stockdiary.domain.analysis.dto.WebClientResDto;
import com.ogd.stockdiary.domain.analysis.port.AnalysisWebClient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class AnalysisService {
  //  private final AnalysisClient analysisClient;
  private final RequestDtoFactory requestDtoFactory;
  private final AnalysisWebClient analysisClient;

  public Flux<WebClientResDto> analyze(
      String market, String symbol, LocalDateTime time, String modelName) {
    RequestDto requestDto = requestDtoFactory.createRequestDto(market, symbol, time);

    Flux<WebClientResDto> responseDtoFlux = analysisClient.analysisMarket(modelName, requestDto);

    return responseDtoFlux;
  }
}
