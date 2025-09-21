package com.ogd.stockdiary.domain.stock.repository;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import java.util.List;

public interface StockRepository {
  List<Stock> findByCompanyName(String companyName);

  SliceContent<Stock> findByCompanyNameSlice(String nextCursor, String companyName, int size);
}
