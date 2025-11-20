package com.ogd.stockdiary.application.report.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ogd.stockdiary.application.report.service.StockDataIngestionBatch;

import lombok.RequiredArgsConstructor;

/**
 * 로컬 개발 및 테스트를 위한 임시 컨트롤러입니다.
 * 프로덕션에는 포함되지 않도록 @Profile("local")을 사용합니다.
 * application.yml에서 spring.profiles.active=local 로 설정해야 활성화됩니다.
 */
@Profile("local") // "local" 프로필일 때만 이 컨트롤러가 활성화됩니다.
@RestController
@RequiredArgsConstructor
public class BatchTestController {

    private final StockDataIngestionBatch stockDataIngestionBatch;
    private final VectorStore vectorStore;

    /**
     * 주식 데이터 수집 배치를 수동으로 실행합니다.
     */
    @GetMapping("/run-stock-batch")
    public String runStockBatch() {
        // 스케줄러의 메서드를 직접 호출합니다.
        stockDataIngestionBatch.ingestDailyStockData();
        return "Stock Data Ingestion Batch has been triggered successfully. Check the logs.";
    }

    /**
     * ChromaDB에 저장된 데이터를 검색하여 확인합니다.
     *
     * @param query
     *            검색할 텍스트
     * @return 유사도 높은 상위 5개 문서의 내용
     */
    @GetMapping("/query-chroma")
    public List<String> queryChromaDB(@RequestParam("q") String query) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(query)
            .topK(5)
            .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        return similarDocuments.stream()
            .map(Document::getText)
            .collect(Collectors.toList());
    }
}
