package com.ogd.stockdiary.application.user.port.out.oauth.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "kakao")
@Getter
@Setter
public class KakaoProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String adminKey;
}
