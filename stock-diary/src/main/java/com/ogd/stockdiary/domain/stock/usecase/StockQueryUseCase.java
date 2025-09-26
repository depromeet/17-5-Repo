package com.ogd.stockdiary.domain.stock.usecase;

import java.util.List;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.dto.response.StockSearchResponse;
import com.ogd.stockdiary.domain.stock.entity.Stock;

public interface StockQueryUseCase {
    List<Stock> findByCompanyNameLike(String companyName);

    SliceContent<StockSearchResponse> findByCompanyNameSlice(
            String nextCursor, String companyName, int size);
}
