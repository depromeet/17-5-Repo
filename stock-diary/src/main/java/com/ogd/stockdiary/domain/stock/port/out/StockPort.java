package com.ogd.stockdiary.domain.stock.port.out;

import java.time.LocalDate;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;

public interface StockPort {
    String getToken();

    StockChartData getChartData(
        String market,
        String symbol,
        LocalDate endDate,
        StockInterval interval
    );
}
