package com.ogd.stockdiary.domain.stock.repository;

import java.util.List;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.entity.Stock;

public interface StockRepository {
    List<Stock> findByCompanyName(String companyName);

    SliceContent<Stock> findByCompanyNameSlice(String nextCursor, String companyName, int size);

    List<Stock> findAllByCodeIn(List<String> codes);

}
