package com.ogd.stockdiary.domain.stock.usecase;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import java.time.LocalDate;

public interface ChartQueryUseCase {

  StockChartData getStockChart(
      String market, String symbol, LocalDate startDate, LocalDate endDate, StockInterval interval);
}
