package com.ogd.stockdiary.application.stock.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.dto.response.StockSearchResponse;
import com.ogd.stockdiary.domain.stock.entity.Market;
import com.ogd.stockdiary.domain.stock.usecase.StockQueryUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Stock Search", description = "주식 검색")
public class StockSearchController {
    private final StockQueryUseCase stockQueryUseCase;

    public StockSearchController(StockQueryUseCase stockQueryUseCase) {
        this.stockQueryUseCase = stockQueryUseCase;
    }

    @Operation(summary = "주식 검색(Legacy)", description = "종목명 또는 종목 코드로 주식을 검색합니다.")
    @Deprecated(since = "2025-09-20", forRemoval = true)
    @GetMapping("/stock/search")
    public HttpApiResponse<List<StockSearchResponse>> searchStock(
            @Parameter(description = "검색 키워드 (종목명 또는 종목 코드)", required = true) @RequestParam
                    String query) {

        List<StockSearchResponse> mockData =
                Arrays.asList(new StockSearchResponse(Market.NAS, "TSLA", "테슬라"));

        return HttpApiResponse.of(mockData);
    }

    @Operation(summary = "주식 검색 (페이지네이션)", description = "종목명 또는 종목 코드로 주식을 무한스크롤 방식으로 검색합니다.")
    @GetMapping("/stock/slice")
    public HttpApiResponse<SliceContent<StockSearchResponse>> searchStockSlice(
            @Parameter(description = "검색 키워드 (종목명 또는 종목 코드)", required = true) @RequestParam
                    String companyName,
            @Parameter(description = "다음 페이지 커서 (첫 페이지는 null)") @RequestParam(required = false)
                    String nextCursor,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10")
                    int size) {
        SliceContent<StockSearchResponse> response =
                stockQueryUseCase.findByCompanyNameSlice(nextCursor, companyName, size);

        return HttpApiResponse.of(response);
    }
}
