package com.ogd.stockdiary.domain.stock.dto.response;

import com.ogd.stockdiary.domain.stock.entity.Market;
import com.ogd.stockdiary.domain.stock.entity.Stock;

public record StockSearchResponse(Market market, String code, String companyName) {
    public static StockSearchResponse from(Stock stock) {
        return new StockSearchResponse(stock.getMarket(), stock.getCode(), stock.getCompanyName());
    }
}
