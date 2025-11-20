package com.ogd.stockdiary.application.config.batch;

import java.time.Duration;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

public class RateLimiterConfigFactory {

    public static RateLimiter createRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(10)
            .timeoutDuration(Duration.ofMillis(500))
            .build();

        return RateLimiter.of("kisApiRateLimiter", config);
    }
}
