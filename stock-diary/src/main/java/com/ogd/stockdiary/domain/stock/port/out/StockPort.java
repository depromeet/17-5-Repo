package com.ogd.stockdiary.domain.stock.port.out;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import java.time.LocalDate;

public interface StockPort {
  String getToken();

  StockChartData getChartData(
      String market, String symbol, LocalDate endDate, StockInterval interval);
}
