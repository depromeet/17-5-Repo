package com.ogd.stockdiary;

import org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ChromaVectorStoreAutoConfiguration.class})
@EnableAsync
@EnableCaching
@EnableScheduling
public class StockDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDiaryApplication.class, args);
    }
}
