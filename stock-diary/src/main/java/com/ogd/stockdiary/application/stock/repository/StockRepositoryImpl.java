package com.ogd.stockdiary.application.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.entity.Market;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;

@Repository
public class StockRepositoryImpl implements StockRepository {
    private final JpaStockRepository jpaStockRepository;

    public StockRepositoryImpl(JpaStockRepository jpaStockRepository) {
        this.jpaStockRepository = jpaStockRepository;
    }

    @Override
    public List<Stock> findByCompanyName(String companyName) {
        return jpaStockRepository.findByCompanyNameLike(companyName);
    }

    @Override
    public SliceContent<Stock> findByCompanyNameSlice(
        String nextCursor, String companyName, int size) {
        List<Stock> content;

        if (nextCursor == null) {
            content = jpaStockRepository.findByCompanyNameLikeOrderByIdDesc(
                companyName, PageRequest.of(0, size));
        } else {
            content = jpaStockRepository.findByCompanyNameLikeOrderByIdDesc(
                companyName, Integer.parseInt(nextCursor), PageRequest.of(0, size));
        }

        String id = content.isEmpty() ? null : content.get(content.size() - 1).getId().toString();

        return new SliceContent<>(content, id);
    }

    @Override
    public List<Stock> findAllByCodeIn(List<String> codes) {
        return jpaStockRepository.findAllByCodeIn(codes);
    }

    @Override
    public Stock findByCode(String code) {
        return jpaStockRepository.findByCode(code);
    }

    @Override
    public Optional<Stock> findByCodeAndMarket(String code, Market market) {
        return jpaStockRepository.findByCodeAndMarket(code, market);
    }

    @Override
    public Stock save(Stock stock) {
        return jpaStockRepository.save(stock);
    }

    @Override
    public List<Stock> findAll() {
        return jpaStockRepository.findAll();
    }
}
