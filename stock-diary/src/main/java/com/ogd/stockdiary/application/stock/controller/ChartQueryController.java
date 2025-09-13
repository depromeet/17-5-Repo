package com.ogd.stockdiary.application.stock.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ogd.stockdiary.application.stock.port.out.dto.TokenRequest;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenResponse;
import com.ogd.stockdiary.application.stock.port.out.HanStockFeignClient;

@RestController
@RequestMapping("/api/v1/charts")
public class ChartQueryController {

    private final HanStockFeignClient hanStockFeignClient;

    public ChartQueryController(HanStockFeignClient hanStockFeignClient) {
        this.hanStockFeignClient = hanStockFeignClient;
    }

    @PostMapping("/test")
    public TokenResponse test() {
        TokenRequest request = new TokenRequest(
            "client_credentials",
            "wS6taqks0+FJmyHxrFouol6EOLJhSMhyLrvsUHcqlvHxEVxa/TYXqFqD0M/eOMWGmnPPB+X/fuqr8LnJJK/ZKMlcDOxVWo5BU85hom/PgpP0H4p5pYJjESLXAuRkdrrnp/UtapmJhOVOYQrkXqz2TkqCkYGW2zwgwYXPBGLisyHWWSRI8C0=",
            "PSOA5a8EUEzQnsbb0Stieigj9n8jUUBiwJ0A"
        );

        return hanStockFeignClient.getToken(request);
    }
}
