// package com.ogd.stockdiary.domain.analysis.port;
//
// import com.ogd.stockdiary.domain.analysis.dto.RequestDto;
// import com.ogd.stockdiary.domain.analysis.dto.ResponseDto;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
//
// @FeignClient(name = "analysis-service", url = "https://clovastudio.stream.ntruss.com")
// @Qualifier("analysis-service") public interface AnalysisClient {
//
//  @PostMapping("/v1/chat-completions/{modelName}")
//  ResponseDto analysisMarket(
//      @PathVariable("modelName") String modelName, @RequestBody RequestDto requestDto);
// }
