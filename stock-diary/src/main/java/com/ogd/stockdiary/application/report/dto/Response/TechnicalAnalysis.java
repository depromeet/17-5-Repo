package com.ogd.stockdiary.application.report.dto.Response;

public record TechnicalAnalysis(
    String rsi,
    String macd,
    String bollingerBands,
    String movingAverage,
    Integer support,
    Integer resistance,
    String signal) {
    public static TechnicalAnalysis onCreate(String rsi, String macd, String bollingerBands, String movingAverage,
        Integer support, Integer resistance, String signal) {
        return new TechnicalAnalysis(rsi, macd, bollingerBands, movingAverage, support, resistance, signal);
    }
}
