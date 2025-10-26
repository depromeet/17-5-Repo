package com.ogd.stockdiary.application.user.port.out.oauth.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "apple")
@Getter
@Setter
public class AppleProperties {

    // Apple ID 서비스의 고정된 Audience URL(변경되지 않는 다는것을 파악)
    public static final String APPLE_AUD = "https://appleid.apple.com";

    private String clientId;
    private String redirectUri;
    private String teamId;
    private String keyId;
    private String privateKey;
    private String keyFilePath; // .p8 파일 경로 (로컬 테스트용)
}
