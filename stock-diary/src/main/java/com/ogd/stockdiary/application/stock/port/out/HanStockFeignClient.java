package com.ogd.stockdiary.application.stock.port.out;

import com.ogd.stockdiary.application.stock.port.out.dto.DailyPriceResponse;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenRequest;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "han-stock", url = "https://openapivts.koreainvestment.com:29443")
public interface HanStockFeignClient {

  @PostMapping(value = "/oauth2/tokenP", consumes = "application/json")
  TokenResponse getToken(@RequestBody TokenRequest request);

  @GetMapping("/uapi/overseas-price/v1/quotations/dailyprice")
  DailyPriceResponse getDailyPrice(
      @RequestHeader("authorization") String authorization,
      @RequestHeader("appkey") String appkey,
      @RequestHeader("appsecret") String appsecret,
      @RequestHeader("tr_id") String trId,
      @RequestParam("AUTH") String auth,
      @RequestParam("EXCD") String market,
      @RequestParam("SYMB") String symbol,
      @RequestParam("GUBN") String period,
      @RequestParam("BYMD") String baseDate,
      @RequestParam("MODP") String modifiedPrice);
}
