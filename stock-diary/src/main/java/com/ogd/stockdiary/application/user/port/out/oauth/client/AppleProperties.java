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
    private String clientId;
    private String redirectUri;
    private String aud;
    private String teamId;
    private String keyId;
    private String privateKey;
}
