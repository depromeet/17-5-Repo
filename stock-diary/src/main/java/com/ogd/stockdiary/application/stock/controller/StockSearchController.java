package com.ogd.stockdiary.application.stock.controller;

import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Stock Search", description = "주식 검색")
public class StockSearchController {

    @Operation(summary = "주식 검색", description = "종목명 또는 종목 코드로 주식을 검색합니다.")
    @GetMapping("/stock/search")
    public HttpApiResponse<List<StockSearchResponse>> searchStock(
            @Parameter(description = "검색 키워드 (종목명 또는 종목 코드)", required = true)
            @RequestParam String query) {

        List<StockSearchResponse> mockData = Arrays.asList(
                new StockSearchResponse("TSLA", "테슬라", "NAS")
        );

        return HttpApiResponse.of(mockData);
    }

    public static class StockSearchResponse {
        private String symbol;
        private String title;
        private String market;

        public StockSearchResponse(String symbol, String title, String market) {
            this.symbol = symbol;
            this.title = title;
            this.market = market;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getTitle() {
            return title;
        }

        public String getMarket() {
            return market;
        }
    }
}