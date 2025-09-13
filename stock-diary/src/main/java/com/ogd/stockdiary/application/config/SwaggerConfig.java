package com.ogd.stockdiary.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Stock Diary API", version = "1.0"))
@SecurityScheme(
    name = SwaggerConfig.SECURITY_SCHEME_NAME,
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token Authentication")
public class SwaggerConfig {
  public static final String SECURITY_SCHEME_NAME = "BearerAuth";

  @Value("${server.url:http://localhost:8080}")
  private String serverUrl;

  @Bean
  public OpenAPI openAPI() {
    Server server = new Server();
    server.setUrl(serverUrl);

    return new OpenAPI().addServersItem(server);
  }
}
