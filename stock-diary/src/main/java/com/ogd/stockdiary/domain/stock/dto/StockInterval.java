package com.ogd.stockdiary.domain.stock.dto;

public enum StockInterval {
  DAILY("0"),
  WEEKLY("1"),
  MONTHLY("2");

  private final String code;

  StockInterval(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
