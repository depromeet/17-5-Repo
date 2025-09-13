package com.ogd.stockdiary.application.stock.port.out;

import java.time.LocalDate;

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
    public StockChartData getChartData(String market, String symbol, LocalDate startDate, LocalDate endDate, StockInterval interval) {
        String token = getToken();
        String authorization = "Bearer " + token;

        DailyPriceResponse response = hanStockFeignClient.getDailyPrice(
            authorization,
            APP_KEY,
            APP_SECRET,
            TR_ID,
            "",
            market,
            symbol,
            interval.getCode(),
            "",
            "1"
        );

        return DailyPriceResponse.toStockChartData(response, market);
    }
}
