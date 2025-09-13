package com.ogd.stockdiary.domain.stock.usecase;

import java.time.LocalDate;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;

public interface ChartQueryUseCase {

    StockChartData getStockChart(
        String market,
        String symbol,
        LocalDate startDate,
        LocalDate endDate,
        StockInterval interval
    );
}
