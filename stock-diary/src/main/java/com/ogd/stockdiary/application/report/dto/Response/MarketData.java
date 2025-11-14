package com.ogd.stockdiary.application.report.dto.Response;

public record MarketData(
    String symbol,
    String trend1w,
    String trend1m,
    String trend6m

) {
    public static MarketData onCreate(String symbol, String trend1w, String trend1m, String trend6m) {
        return new MarketData(symbol, trend1w, trend1m, trend6m);
    }
}
