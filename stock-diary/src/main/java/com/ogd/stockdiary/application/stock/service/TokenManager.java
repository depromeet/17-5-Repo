package com.ogd.stockdiary.application.stock.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.ogd.stockdiary.application.stock.port.out.HanStockFeignClient;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenRequest;
import com.ogd.stockdiary.application.stock.port.out.dto.TokenResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenManager {

    private final HanStockFeignClient hanStockFeignClient;
    private static final String TOKEN_FILE_PATH = "stock_api_token.txt";
    private static final String APP_KEY = "PSOA5a8EUEzQnsbb0Stieigj9n8jUUBiwJ0A";
    private static final String APP_SECRET = "wS6taqks0+FJmyHxrFouol6EOLJhSMhyLrvsUHcqlvHxEVxa/TYXqFqD0M/eOMWGmnPPB+X/fuqr8LnJJK/ZKMlcDOxVWo5BU85hom/PgpP0H4p5pYJjESLXAuRkdrrnp/UtapmJhOVOYQrkXqz2TkqCkYGW2zwgwYXPBGLisyHWWSRI8C0=";

    public TokenManager(HanStockFeignClient hanStockFeignClient) {
        this.hanStockFeignClient = hanStockFeignClient;
    }

    public String getValidToken() {
        try {
            String storedToken = readTokenFromFile();
            if (storedToken != null && !storedToken.isEmpty()) {
                log.info("Using stored token from file");
                return storedToken;
            }
        } catch (Exception e) {
            log.warn("Failed to read token from file: {}", e.getMessage());
        }

        return refreshToken();
    }

    public String refreshToken() {
        try {
            log.info("Refreshing token...");
            TokenRequest request = new TokenRequest("client_credentials", APP_SECRET, APP_KEY);
            TokenResponse response = hanStockFeignClient.getToken(request);
            String newToken = response.getAccessToken();

            saveTokenToFile(newToken);
            log.info("Token refreshed and saved to file");
            return newToken;
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed", e);
        }
    }

    public void invalidateToken() {
        try {
            Path path = Paths.get(TOKEN_FILE_PATH);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Token file deleted due to 4xx error");
            }
        } catch (IOException e) {
            log.warn("Failed to delete token file: {}", e.getMessage());
        }
    }

    private String readTokenFromFile() throws IOException {
        Path path = Paths.get(TOKEN_FILE_PATH);
        if (!Files.exists(path)) {
            return null;
        }

        String content = Files.readString(path);
        if (content.trim().isEmpty()) {
            return null;
        }

        String[] lines = content.split("\n");
        if (lines.length >= 2) {
            String timestamp = lines[0];
            String token = lines[1];

            // 토큰이 24시간 이내인지 확인
            try {
                LocalDateTime tokenTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (tokenTime.plusHours(23).isAfter(LocalDateTime.now())) {
                    return token;
                }
            } catch (Exception e) {
                log.warn("Failed to parse token timestamp: {}", e.getMessage());
            }
        }

        return null;
    }

    private void saveTokenToFile(String token) throws IOException {
        String content = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n" + token;
        Path path = Paths.get(TOKEN_FILE_PATH);
        Files.writeString(path, content);
        log.info("Token saved to file: {}", TOKEN_FILE_PATH);
    }
}