package com.ogd.stockdiary.domain.stock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.dto.response.StockSearchResponse;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;
import com.ogd.stockdiary.domain.stock.usecase.StockQueryUseCase;

@Service
public class StockQueryService implements StockQueryUseCase {
    private final StockRepository stockRepository;

    public StockQueryService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> findByCompanyNameLike(String companyName) {
        return stockRepository.findByCompanyName(companyName);
    }

    @Override
    public SliceContent<StockSearchResponse> findByCompanyNameSlice(
            String nextCursor, String companyName, int size) {
        SliceContent<Stock> stockSlice =
                stockRepository.findByCompanyNameSlice(nextCursor, companyName, size);

        List<StockSearchResponse> responseList =
                stockSlice.content().stream().map(StockSearchResponse::from).toList();

        return new SliceContent<>(responseList, stockSlice.nextCursor());
    }
}
