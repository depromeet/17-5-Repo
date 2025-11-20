package com.ogd.stockdiary.domain.stock.repository;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.entity.Market;
import com.ogd.stockdiary.domain.stock.entity.Stock;

public interface StockRepository {
    List<Stock> findByCompanyName(String companyName);

    SliceContent<Stock> findByCompanyNameSlice(String nextCursor, String companyName, int size);

    List<Stock> findAllByCodeIn(List<String> codes);

    Stock findByCode(String code);

    Optional<Stock> findByCodeAndMarket(String code, Market market);

    Stock save(Stock stock);

    List<Stock> findAll();

}
