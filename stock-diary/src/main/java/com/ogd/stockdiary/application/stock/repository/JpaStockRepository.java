package com.ogd.stockdiary.application.stock.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ogd.stockdiary.domain.stock.entity.Stock;

@RequestMapping
public interface JpaStockRepository extends CrudRepository<Stock, Long> {
    @Query("SELECT s FROM Stock s WHERE s.companyName LIKE %:companyName%")
    List<Stock> findByCompanyNameLike(String companyName);

    @Query("SELECT s FROM Stock s WHERE s.companyName LIKE %:companyName% ORDER BY s.id DESC")
    List<Stock> findByCompanyNameLikeOrderByIdDesc(String companyName, Pageable pageable);

    @Query(
            "SELECT s FROM Stock s WHERE s.companyName LIKE %:companyName% AND s.id < :cursor ORDER BY s.id DESC")
    List<Stock> findByCompanyNameLikeOrderByIdDesc(
            String companyName, Integer cursor, Pageable pageable);
}
