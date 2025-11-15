package com.ogd.stockdiary.application.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 코어 풀과 Max 풀 크기를 I/O 바운드에 맞게 늘리기
        executor.setCorePoolSize(16);

        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(500);
        executor.initialize();

        return executor;

    }
}
