package com.ogd.stockdiary.application.config.batch;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI VectorStore 빈을 수동으로 정의하고,
 * 컬렉션 생성(initializeSchema) 문제를 우회하기 위해
 * 컬렉션 초기화 로직을 CommandLineRunner로 분리한 설정 클래스입니다.
 */
@Configuration
@Slf4j
public class VectorStoreConfig {

    private static final String DEFAULT_TENANT = "default_tenant";
    private static final String DEFAULT_DATABASE = "default_database";

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        String chromaUrl = "http://localhost:8000";
        ChromaApi chromaApi = new ChromaApi(chromaUrl, restClientBuilder, objectMapper);
        return chromaApi;
    }

    @Bean
    @Lazy
    public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
            .tenantName(DEFAULT_TENANT)
            .databaseName(DEFAULT_DATABASE)
            .collectionName("stock-market-data")
            .initializeSchema(false) // 자동 스키마 초기화 비활성화
            .build();
    }
}
