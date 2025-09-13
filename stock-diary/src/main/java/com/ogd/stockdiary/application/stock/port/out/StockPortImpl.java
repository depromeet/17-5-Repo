package com.ogd.stockdiary.application.stock.port.out;

import com.ogd.stockdiary.application.stock.port.out.dto.DailyPriceResponse;
import com.ogd.stockdiary.application.stock.service.TokenManager;
import com.ogd.stockdiary.domain.stock.dto.StockChartData;
import com.ogd.stockdiary.domain.stock.dto.StockInterval;
import com.ogd.stockdiary.domain.stock.port.out.StockPort;
import feign.FeignException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockPortImpl implements StockPort {

  private final HanStockFeignClient hanStockFeignClient;
  private final TokenManager tokenManager;

  private static final String APP_KEY = "PSOA5a8EUEzQnsbb0Stieigj9n8jUUBiwJ0A";
  private static final String APP_SECRET =
      "wS6taqks0+FJmyHxrFouol6EOLJhSMhyLrvsUHcqlvHxEVxa/TYXqFqD0M/eOMWGmnPPB+X/fuqr8LnJJK/ZKMlcDOxVWo5BU85hom/PgpP0H4p5pYJjESLXAuRkdrrnp/UtapmJhOVOYQrkXqz2TkqCkYGW2zwgwYXPBGLisyHWWSRI8C0=";
  private static final String TR_ID = "HHDFS76240000";

  public StockPortImpl(HanStockFeignClient hanStockFeignClient, TokenManager tokenManager) {
    this.hanStockFeignClient = hanStockFeignClient;
    this.tokenManager = tokenManager;
  }

  @Override
  public String getToken() {
    return tokenManager.getValidToken();
  }

  @Override
  public StockChartData getChartData(
      String market, String symbol, LocalDate endDate, StockInterval interval) {
    String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    // 첫 번째 시도
    try {
      return callDailyPriceApi(market, symbol, formattedEndDate, interval);
    } catch (FeignException e) {
      if (is4xxError(e)) {
        log.warn("4xx error occurred, invalidating token and retrying: {}", e.getMessage());

        // 현재 토큰 파일 삭제 후 새 토큰으로 두 번째 시도
        tokenManager.invalidateToken();
        try {
          return callDailyPriceApi(market, symbol, formattedEndDate, interval);
        } catch (Exception retryException) {
          log.error("Retry failed: {}", retryException.getMessage());
          throw new RuntimeException("API call failed after token refresh", retryException);
        }
      } else {
        log.error("Non-4xx error occurred: {}", e.getMessage());
        throw new RuntimeException("API call failed", e);
      }
    }
  }

  private StockChartData callDailyPriceApi(
      String market, String symbol, String formattedEndDate, StockInterval interval) {
    String token = tokenManager.getValidToken();
    String authorization = "Bearer " + token;

    DailyPriceResponse response =
        hanStockFeignClient.getDailyPrice(
            authorization,
            APP_KEY,
            APP_SECRET,
            TR_ID,
            "",
            market,
            symbol,
            interval.getCode(),
            formattedEndDate,
            "1");

    return DailyPriceResponse.toStockChartData(response, market);
  }

  private boolean is4xxError(FeignException e) {
    int status = e.status();
    return status >= 400 && status < 500;
  }
}
