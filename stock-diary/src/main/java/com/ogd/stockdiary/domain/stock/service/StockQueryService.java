package com.ogd.stockdiary.domain.stock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.domain.stock.dto.response.StockSearchResponse;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;
import com.ogd.stockdiary.domain.stock.usecase.StockQueryUseCase;

@Service
public class StockQueryService implements StockQueryUseCase {
    private final StockRepository stockRepository;
    private final FileClientPort fileClientPort;

    public StockQueryService(StockRepository stockRepository, FileClientPort fileClientPort) {
        this.stockRepository = stockRepository;
        this.fileClientPort = fileClientPort;
    }

    @Override
    public List<Stock> findByCompanyNameLike(String companyName) {
        return stockRepository.findByCompanyName(companyName);
    }

    @Override
    public SliceContent<StockSearchResponse> findByCompanyNameSlice(
        String nextCursor, String companyName, int size) {
        SliceContent<Stock> stockSlice = stockRepository.findByCompanyNameSlice(nextCursor, companyName, size);

        List<StockSearchResponse> responseList = stockSlice.content().stream()
            .map(stock -> {
                String logo = null;
                if (stock.getLogo() != null && !stock.getLogo().isEmpty()) {
                    logo = fileClientPort.getDownloadPreSignedUrl(stock.getLogo(), 86400); // 24시간
                }
                return StockSearchResponse.of(stock, logo);
            })
            .toList();

        return new SliceContent<>(responseList, stockSlice.nextCursor());
    }
}
