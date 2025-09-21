package com.ogd.stockdiary.domain.stock.usecase;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.dto.response.StockSearchResponse;
import com.ogd.stockdiary.domain.stock.entity.Stock;

import java.util.List;

public interface StockQueryUseCase {
    List<Stock> findByCompanyNameLike(String companyName);
    SliceContent<StockSearchResponse> findByCompanyNameSlice(String nextCursor, String companyName, int size);
}
