package com.ogd.stockdiary.application.report.dto.Response;

public record FundamentalAnalysis(
    String earnings,
    String peRatio,
    String sectorTrend,
    String macroFactors,
    String recommendation) {
    public static FundamentalAnalysis onCreate(String earnings, String peRatio, String sectorTrend, String macroFactors,
        String recommendation) {
        return new FundamentalAnalysis(earnings, peRatio, sectorTrend, macroFactors, recommendation);
    }
}
