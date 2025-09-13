package com.ogd.stockdiary.application.stock.port.out;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.ogd.stockdiary.application.stock.port.out.dto.DailyPriceResponse;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenRequest;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenResponse;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.port.out.StockPort;

@Component
public class StockPortImpl implements StockPort {

    private final HanStockFeignClient hanStockFeignClient;

    private static final String APP_KEY = "PSOA5a8EUEzQnsbb0Stieigj9n8jUUBiwJ0A";
    private static final String APP_SECRET = "wS6taqks0+FJmyHxrFouol6EOLJhSMhyLrvsUHcqlvHxEVxa/TYXqFqD0M/eOMWGmnPPB+X/fuqr8LnJJK/ZKMlcDOxVWo5BU85hom/PgpP0H4p5pYJjESLXAuRkdrrnp/UtapmJhOVOYQrkXqz2TkqCkYGW2zwgwYXPBGLisyHWWSRI8C0=";
    private static final String TR_ID = "HHDFS76240000";

    public StockPortImpl(HanStockFeignClient hanStockFeignClient) {
        this.hanStockFeignClient = hanStockFeignClient;
    }

    @Override
    public String getToken() {
        TokenRequest request = new TokenRequest("client_credentials", APP_SECRET, APP_KEY);
        TokenResponse response = hanStockFeignClient.getToken(request);
        return response.getAccessToken();
    }

    @Override
    public StockChartData getChartData(String market, String symbol, LocalDate endDate, StockInterval interval) {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjJjZTM5ZjhmLWRmNzMtNDBhZS1iNjM4LWFhNWU0Y2VkODUyYiIsInByZHRfY2QiOiIiLCJpc3MiOiJ1bm9ndyIsImV4cCI6MTc1Nzg2MzEyMywiaWF0IjoxNzU3Nzc2NzIzLCJqdGkiOiJQU09BNWE4RVVFelFuc2JiMFN0aWVpZ2o5bjhqVVVCaXdKMEEifQ.Z7q1cA4AI74Hrjfmg62tukt6_tsm1upj8azmV4lEnOEGD3hIP62ZHf8HcUVyB1Qxgy0rz-kdalxU_Ih0SUIRXA";
        String authorization = "Bearer " + token;

        // LocalDate를 yyyyMMdd 형식으로 변환
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        DailyPriceResponse response = hanStockFeignClient.getDailyPrice(
            authorization,
            APP_KEY,
            APP_SECRET,
            TR_ID,
            "",
            market,
            symbol,
            interval.getCode(),
            formattedEndDate,
            "1"
        );

        return DailyPriceResponse.toStockChartData(response, market);
    }
}
