package com.ogd.stockdiary.application.report.dto.Response;

public record Research(
    String marketContext,
    String investmentThesis,
    String riskFactors,
    String priceTarget,
    String technicalSummary,
    String fundamentalSummary

) {
    public static Research onCreate(String marketContext, String investmentThesis, String riskFactors,
        String priceTarget, String technicalSummary, String fundamentalSummary) {
        return new Research(marketContext, investmentThesis, riskFactors, priceTarget, technicalSummary,
            fundamentalSummary);
    }
}
