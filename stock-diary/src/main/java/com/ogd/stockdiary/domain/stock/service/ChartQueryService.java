package com.ogd.stockdiary.domain.stock.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.port.out.StockPort;
import com.ogd.stockdiary.domain.stock.usecase.ChartQueryUseCase;

@Service
public class ChartQueryService implements ChartQueryUseCase {

    private final StockPort stockPort;

    public ChartQueryService(StockPort stockPort) {
        this.stockPort = stockPort;
    }

    @Override
    public StockChartData getStockChart(String market, String symbol, LocalDate startDate, LocalDate endDate, StockInterval interval) {
        return stockPort.getChartData(market, symbol, interval);
    }
}
