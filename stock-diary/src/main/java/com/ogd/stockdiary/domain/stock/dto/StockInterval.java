package com.ogd.stockdiary.domain.stock.dto;

public enum StockInterval {
    DAILY("2"),
    WEEKLY("3"),
    MONTHLY("4");

    private final String code;

    StockInterval(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}