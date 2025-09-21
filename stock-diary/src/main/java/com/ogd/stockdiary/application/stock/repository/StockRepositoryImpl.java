package com.ogd.stockdiary.application.stock.repository;

import com.ogd.stockdiary.common.httpresponse.SliceContent;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

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
      content =
          jpaStockRepository.findByCompanyNameLikeOrderByIdDesc(
              companyName, PageRequest.of(0, size));
    } else {
      content =
          jpaStockRepository.findByCompanyNameLikeOrderByIdDesc(
              companyName, Integer.parseInt(nextCursor), PageRequest.of(0, size));
    }

    String id = content.isEmpty() ? null : content.get(content.size() - 1).getId().toString();

    return new SliceContent<>(content, id);
  }
}
