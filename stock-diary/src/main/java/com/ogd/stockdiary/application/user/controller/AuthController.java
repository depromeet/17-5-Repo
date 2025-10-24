package com.ogd.stockdiary.application.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.application.user.dto.RefreshTokenRequest;
import com.ogd.stockdiary.application.user.dto.RefreshTokenResponse;
import com.ogd.stockdiary.application.user.dto.SocialLoginRequest;
import com.ogd.stockdiary.application.user.dto.SocialLoginResponse;
import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.service.AuthResult;
import com.ogd.stockdiary.domain.user.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증", description = "OAuth 소셜 로그인 및 JWT 토큰 인증 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인 (Kakao, Apple)", description = """
        ## 소셜 로그인 API

        OAuth 2.0 Authorization Code Flow를 사용한 소셜 로그인 API입니다.

        ### 사용 방법

        #### 1. Kakao 로그인
        ```json
        {
          "provider": "KAKAO",
          "authCode": "SDK로부터 받은 authCode",
          "redirectUri": "kakao{YOUR_APP_KEY}://oauth",  // SDK가 사용한 redirect_uri
          "email": null,     // null 가능 (idToken에서 자동 추출)
          "nickname": null   // null 가능 (idToken에서 자동 추출)
        }
        ```

        #### 2. Apple 첫 로그인
        ```json
        {
          "provider": "APPLE",
          "authCode": "SDK로부터 받은 authCode",
          "redirectUri": "your.app.bundle.id",
          "email": "user@example.com",    // Apple SDK가 제공한 값 (필수)
          "nickname": "홍길동"             // Apple SDK가 제공한 값 (필수)
        }
        ```

        #### 3. Apple 재로그인
        ```json
        {
          "provider": "APPLE",
          "authCode": "SDK로부터 받은 authCode",
          "redirectUri": "your.app.bundle.id",
          "email": null,     // null 가능 (DB에 저장된 값 사용)
          "nickname": null   // null 가능 (DB에 저장된 값 사용)
        }
        ```

        ### 주의사항
        - **redirectUri**: authCode 획득 시 사용한 redirect_uri와 동일해야 함 (OAuth 2.0 보안 요구사항)
        - **Apple**: 첫 로그인 시에만 email/nickname 제공, 재로그인 시에는 null
        - **Kakao**: email/nickname은 idToken payload에서 자동 추출되므로 null 전달 가능
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialLoginResponse.class), examples = @ExampleObject(value = """
            {
              "userId": 1,
              "nickname": "홍길동",
              "email": "user@example.com",
              "profileImageUrl": "https://...",
              "isNewUser": true,
              "accessToken": "eyJhbGc...",
              "refreshToken": "eyJhbGc..."
            }
            """))),
        @ApiResponse(responseCode = "400", description = "OAuth 토큰 교환 실패 또는 잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": "AUTH_001",
              "message": "Kakao OAuth 토큰 교환 실패",
              "data": {
                "provider": "KAKAO",
                "httpStatus": 400,
                "error": "invalid_grant",
                "error_description": "authorization code not found",
                "error_code": "KOE320"
              }
            }
            """)))
    })
    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(
        @RequestBody SocialLoginRequest request) {
        AuthResult authResult = authService.socialLogin(
            request.getProvider(),
            request.getAuthCode(),
            request.getRedirectUri(),
            request.getEmail(),
            request.getNickname());

        SocialLoginResponse response = SocialLoginResponse.from(authResult);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apple/callback")
    public ResponseEntity<SocialLoginResponse> appleCallback(@RequestParam("code") String code) {
        log.info("Apple OAuth callback received with code: {}", code);

        AuthResult authResult = authService.socialLogin(
            com.ogd.stockdiary.domain.user.entity.OAuthProvider.APPLE,
            code,
            null, // redirectUri는 application.yml의 설정 사용
            null,
            null);

        SocialLoginResponse response = SocialLoginResponse.from(authResult);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<SocialLoginResponse> kakaoCallback(@RequestParam("code") String code) {
        log.info("Kakao OAuth callback received with code: {}", code);

        AuthResult authResult = authService.socialLogin(
            OAuthProvider.KAKAO,
            code,
            null, // redirectUri는 application.yml의 설정 사용
            null,
            null);

        SocialLoginResponse response = SocialLoginResponse.from(authResult);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            log.info("Token refresh requested");
            String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/social/unlink/{userId}")
    public ResponseEntity<Void> unlinkSocialAccount(@PathVariable Long userId) {
        try {
            authService.unlinkSocialAccount(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Social account unlink failed for user: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
