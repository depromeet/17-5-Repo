package com.ogd.stockdiary.application.config.batch;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChromaCollectionInitializer {

    private final ChromaApi chromaApi;

    private static final String TENANT_NAME = "default_tenant";
    private static final String DATABASE_NAME = "default_database";
    private static final String COLLECTION_NAME = "stock-market-data";

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCollection() {
        try {
            var tenant = chromaApi.getTenant(TENANT_NAME);
            if (tenant == null) {
                log.info("Tenant {} not found. Creating tenant.", TENANT_NAME);
                chromaApi.createTenant(TENANT_NAME);
            } else {
                log.info("Tenant {} already exists.", TENANT_NAME);
            }

            var database = chromaApi.getDatabase(TENANT_NAME, DATABASE_NAME);
            if (database == null) {
                log.info("Database {} not found. Creating database.", DATABASE_NAME);
                chromaApi.createDatabase(TENANT_NAME, DATABASE_NAME);
            } else {
                log.info("Database {} already exists.", DATABASE_NAME);
            }

            var collection = chromaApi.getCollection(TENANT_NAME, DATABASE_NAME, COLLECTION_NAME);
            if (collection == null) {
                log.info("Collection {} not found. Creating collection.", COLLECTION_NAME);
                chromaApi.createCollection(TENANT_NAME, DATABASE_NAME,
                    new ChromaApi.CreateCollectionRequest(COLLECTION_NAME));
            } else {
                log.info("Collection {} already exists.", COLLECTION_NAME);
            }
        } catch (Exception e) {
            log.error("Error during collection initialization", e);
            // 필요 시 예외 처리 추가
        }
    }
}
