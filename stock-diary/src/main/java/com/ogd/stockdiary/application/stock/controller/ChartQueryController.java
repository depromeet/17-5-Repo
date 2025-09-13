package com.ogd.stockdiary.application.stock.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ogd.stockdiary.application.stock.dto.ChartResponse;
import com.ogd.stockdiary.application.stock.port.out.HanStockFeignClient;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenRequest;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.usecase.ChartQueryUseCase;

@RestController
@RequestMapping("/api/v1")
public class ChartQueryController {

    private final HanStockFeignClient hanStockFeignClient;
    private final ChartQueryUseCase chartQueryUseCase;

    public ChartQueryController(HanStockFeignClient hanStockFeignClient, ChartQueryUseCase chartQueryUseCase) {
        this.hanStockFeignClient = hanStockFeignClient;
        this.chartQueryUseCase = chartQueryUseCase;
    }

    @GetMapping("/stock/charts/{market}/{symbol}")
    public HttpApiResponse<ChartResponse.ChartData> getStockCharts(
        @PathVariable String market,
        @PathVariable String symbol,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "DAILY") StockInterval interval
    ) {
        StockChartData stockChartData = chartQueryUseCase.getStockChart(market, symbol, startDate, endDate, interval);

        List<ChartResponse.ChartItem> chartItems = stockChartData.getChartData().stream()
            .map(item -> new ChartResponse.ChartItem(
                item.getDate(),
                item.getOpen(),
                item.getHigh(),
                item.getLow(),
                item.getClose(),
                item.getVolume()
            ))
            .collect(Collectors.toList());

        ChartResponse.ChartData chartData = new ChartResponse.ChartData(
            stockChartData.getCurrency(),
            chartItems
        );

        return HttpApiResponse.of(chartData);
    }

    @PostMapping("/charts/test")
    public TokenResponse test() {
        TokenRequest request = new TokenRequest(
            "client_credentials",
            "wS6taqks0+FJmyHxrFouol6EOLJhSMhyLrvsUHcqlvHxEVxa/TYXqFqD0M/eOMWGmnPPB+X/fuqr8LnJJK/ZKMlcDOxVWo5BU85hom/PgpP0H4p5pYJjESLXAuRkdrrnp/UtapmJhOVOYQrkXqz2TkqCkYGW2zwgwYXPBGLisyHWWSRI8C0=",
            "PSOA5a8EUEzQnsbb0Stieigj9n8jUUBiwJ0A"
        );

        return hanStockFeignClient.getToken(request);
    }
}
