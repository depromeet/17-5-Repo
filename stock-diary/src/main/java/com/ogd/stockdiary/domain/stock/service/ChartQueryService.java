package com.ogd.stockdiary.domain.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockChartItem;
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
        StockChartData allData = stockPort.getChartData(market, symbol, endDate, interval);

        List<StockChartItem> filteredChartData = allData.getChartData().stream()
            .filter(item -> !item.getDate().isBefore(startDate) && !item.getDate().isAfter(endDate))
            .collect(Collectors.toList());

        return new StockChartData(allData.getCurrency(), filteredChartData);
    }
}
