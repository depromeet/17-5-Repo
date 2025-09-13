package com.ogd.stockdiary.application.stock.port.out.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockChartItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyPriceResponse {

    @JsonProperty("output1")
    private DailyPriceOutput1 output1;

    @JsonProperty("output2")
    private List<DailyPriceItem> output2;

    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    public static StockChartData toStockChartData(DailyPriceResponse response, String market) {
        String currency = getCurrencyByMarket(market);

        List<StockChartItem> chartItems = response.getOutput2().stream()
            .map(DailyPriceResponse::toStockChartItem)
            .collect(Collectors.toList());

        return new StockChartData(currency, chartItems);
    }

    private static StockChartItem toStockChartItem(DailyPriceItem item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(item.getDate(), formatter);

        BigDecimal open = new BigDecimal(item.getOpenPrice());
        BigDecimal high = new BigDecimal(item.getHighPrice());
        BigDecimal low = new BigDecimal(item.getLowPrice());
        BigDecimal close = new BigDecimal(item.getClosePrice());
        Long volume = Long.parseLong(item.getTradingVolume());

        return new StockChartItem(date, open, high, low, close, volume);
    }

    private static String getCurrencyByMarket(String market) {
        switch (market.toUpperCase()) {
            case "NAS":
            case "NASDAQ":
            case "NYSE":
            case "AMEX":
                return "USD";
            case "TSE":
                return "JPY";
            case "LSE":
                return "GBP";
            case "FRA":
                return "EUR";
            case "HKG":
                return "HKD";
            default:
                return "USD";
        }
    }
}