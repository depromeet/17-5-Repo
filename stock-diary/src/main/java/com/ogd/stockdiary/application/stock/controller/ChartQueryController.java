package com.ogd.stockdiary.application.stock.controller;

import com.ogd.stockdiary.application.stock.dto.ChartResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.usecase.ChartQueryUseCase;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ChartQueryController {

  private final ChartQueryUseCase chartQueryUseCase;

  public ChartQueryController(ChartQueryUseCase chartQueryUseCase) {
    this.chartQueryUseCase = chartQueryUseCase;
  }

  @GetMapping("/stock/charts/{market}/{symbol}")
  public HttpApiResponse<ChartResponse.ChartData> getStockCharts(
      @PathVariable String market,
      @PathVariable String symbol,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(defaultValue = "DAILY") StockInterval interval) {
    StockChartData stockChartData =
        chartQueryUseCase.getStockChart(market, symbol, startDate, endDate, interval);

    List<ChartResponse.ChartItem> chartItems =
        stockChartData.getChartData().stream()
            .map(
                item ->
                    new ChartResponse.ChartItem(
                        item.getDate(),
                        item.getOpen(),
                        item.getHigh(),
                        item.getLow(),
                        item.getClose(),
                        item.getVolume()))
            .collect(Collectors.toList());

    ChartResponse.ChartData chartData =
        new ChartResponse.ChartData(stockChartData.getCurrency(), chartItems);

    return HttpApiResponse.of(chartData);
  }
}
