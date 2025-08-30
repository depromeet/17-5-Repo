package com.ogd.stockdiary.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple")
@Getter
@Setter
public class AppleProperties {
    private String clientId;
    private String redirectUri;
    private String aud;
    private String teamId;
    private String keyId;
    private String privateKey;
}