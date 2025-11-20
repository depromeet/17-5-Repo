package com.ogd.stockdiary.application.report.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ogd.stockdiary.application.config.batch.RateLimiterConfigFactory;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.port.out.StockPort;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StockDataIngestionBatch {

    private final StockRepository stockRepository;
    private final StockPort stockPort;
    private final VectorStore vectorStore;
    private final Executor executor;
    private final RateLimiter rateLimiter;
    private final RetryTemplate retryTemplate;

    public StockDataIngestionBatch(StockRepository stockRepository,
        StockPort stockPort,
        VectorStore vectorStore,
        @Qualifier("batchTaskExecutor") Executor executor, RetryTemplate retryTemplate) {
        this.stockRepository = stockRepository;
        this.stockPort = stockPort;
        this.vectorStore = vectorStore;
        this.executor = executor;
        this.rateLimiter = RateLimiterConfigFactory.createRateLimiter();
        this.retryTemplate = retryTemplate;
    }

    /**
     * 매일 새벽 3시에 실행되어 어제 하루의 주가 데이터를 수집하고 ChromaDB에 저장합니다.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void ingestDailyStockData() {

        List<Stock> allStocks = stockRepository.findAll();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // // 1. 병렬 스트림을 사용하여 API 호출을 동시에 처리
        // List<CompletableFuture<Void>> futures = allStocks.stream()
        // .map(stock -> CompletableFuture.runAsync(() -> extracted(stock, yesterday),
        // executor))
        // .toList();
        //
        // // 2. 모든 병렬 작업이 끝날 때까지 대기
        // try {
        // CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        // } catch (Exception e) {
        // log.error("Error occurred while waiting for batch tasks to complete", e);
        // }

        // 순차적 동기 처리
        for (Stock stock : allStocks) {
            try {
                extracted(stock, yesterday);
            } catch (Exception e) {
                log.error("Error occurred while processing stock: {}, error: {}", stock.getCode(), e.getMessage());
            }
        }

    }

    private void extracted(Stock stock, LocalDate targetDate) {
        try {

            // API 호출 제한
            Supplier<StockChartData> supplier = RateLimiter.decorateSupplier(rateLimiter, () -> stockPort
                .getChartData(stock.getMarket().name(), stock.getCode(), targetDate, StockInterval.DAILY));

            StockChartData chartData = retryTemplate.execute(context -> supplier.get());

            // 2. Transform: 날짜로 데이터 필터링
            chartData.getChartData().stream()
                .filter(d -> targetDate.toString().equals(d.getDate()))
                .findFirst()
                .ifPresent(dailyData -> {
                    String documentText = String.format(
                        "%s (%s)의 %s 주가는 시가 %.2f, 고가 %.2f, 저가 %.2f, 종가 %.2f를 기록했으며, 거래량은 %d주였습니다.",
                        stock.getCompanyName(), stock.getCode(), dailyData.getDate(),
                        dailyData.getOpen(), dailyData.getHigh(), dailyData.getLow(),
                        dailyData.getClose(), dailyData.getVolume());

                    // '종목코드-날짜'로 고유 ID 생성
                    String documentId = stock.getCode() + "-" + dailyData.getDate();

                    Map<String, Object> metadata = Map.of(
                        "symbol", stock.getCode(),
                        "date", dailyData.getDate(),
                        "source", "KIS_API");

                    // Document 생성 시 고유 ID를 함께 전달
                    Document document = new Document(documentId, documentText, metadata);

                    // vectorStore.add는 내부적으로 ID를 사용하여 멱등성을 보장할 수 있음 (라이브러리 구현에 따라 다름)
                    // 또는 upsert와 같은 메서드를 사용
                    vectorStore.add(List.of(document));
                });
        } catch (Exception e) {
            log.error("데이터 수집 실패 symbol: {}. Error: {}", stock.getCode(), e.getMessage());

        }
    }
}
