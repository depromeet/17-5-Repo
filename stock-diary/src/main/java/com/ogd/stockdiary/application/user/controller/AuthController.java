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

        OAuth 2.0을 사용한 소셜 로그인 API입니다. 웹, Android, iOS 모두 지원합니다.

        ### 사용 방법

        #### 1. Kakao - iOS SDK
        ```json
        {
          "provider": "KAKAO",
          "authCode": null,
          "idToken": "SDK로부터 받은 idToken",  // oauthToken.idToken
          "redirectUri": null,
          "email": null,     // idToken에서 자동 추출
          "nickname": null   // idToken에서 자동 추출
        }
        ```

        #### 2. Kakao - 웹/Android
        ```json
        {
          "provider": "KAKAO",
          "authCode": "SDK로부터 받은 authCode",
          "idToken": null,
          "redirectUri": "kakao{YOUR_APP_KEY}://oauth",  // SDK가 사용한 redirect_uri
          "email": null,     // idToken에서 자동 추출
          "nickname": null   // idToken에서 자동 추출
        }
        ```

        #### 3. Apple - iOS SDK 첫 로그인
        ```json
        {
          "provider": "APPLE",
          "authCode": null,
          "idToken": "SDK로부터 받은 idToken",  // credential.identityToken
          "redirectUri": null,
          "email": "user@example.com",    // Apple SDK가 제공한 값 (필수)
          "nickname": "홍길동"             // Apple SDK가 제공한 값 (필수)
        }
        ```

        #### 4. Apple - iOS SDK 재로그인
        ```json
        {
          "provider": "APPLE",
          "authCode": null,
          "idToken": "SDK로부터 받은 idToken",
          "redirectUri": null,
          "email": null,     // null 가능 (DB에 저장된 값 사용)
          "nickname": null   // null 가능 (DB에 저장된 값 사용)
        }
        ```

        ### 주의사항
        - **iOS SDK**: idToken 사용 (authCode는 null)
        - **웹/Android**: authCode 사용 (idToken은 null)
        - **redirectUri**: authCode 사용 시에만 필요 (OAuth 2.0 보안 요구사항)
        - **Apple 첫 로그인**: email/nickname 필수, 재로그인 시에는 null 가능
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
            request.getIdToken(),
            request.getRedirectUri(),
            request.getEmail(),
            request.getNickname());

        SocialLoginResponse response = SocialLoginResponse.from(authResult);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/apple/callback", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<SocialLoginResponse> appleCallback(@RequestParam("code") String code) {
        log.info("Apple OAuth callback received with code: {}", code);

        AuthResult authResult = authService.socialLogin(
            com.ogd.stockdiary.domain.user.entity.OAuthProvider.APPLE,
            code,
            null, // idToken
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
            null, // idToken
            null, // redirectUri는 application.yml의 설정 사용
            null,
            null);

        SocialLoginResponse response = SocialLoginResponse.from(authResult);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 갱신 (Refresh Token Rotation)", description = """
        ## 토큰 갱신 API (Refresh Token Rotation)

        Access Token이 만료되었을 때 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.

        ### 보안 기능: Refresh Token Rotation
        - 기존 Refresh Token은 즉시 무효화되며 새로운 Refresh Token이 발급됩니다
        - 탈취된 Refresh Token의 재사용을 방지합니다
        - 클라이언트는 응답으로 받은 새로운 Refresh Token을 저장해야 합니다

        ### 요청 예시
        ```json
        {
          "refreshToken": "eyJhbGc..."
        }
        ```

        ### 응답 예시
        ```json
        {
          "accessToken": "eyJhbGc...",
          "refreshToken": "eyJhbGc..."  // 새로운 Refresh Token
        }
        ```

        ### 주의사항
        - 응답으로 받은 새로운 refreshToken을 반드시 저장해야 합니다
        - 이전 refreshToken은 더 이상 사용할 수 없습니다
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenResponse.class), examples = @ExampleObject(value = """
            {
              "accessToken": "eyJhbGc...",
              "refreshToken": "eyJhbGc..."
            }
            """))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 Refresh Token", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            log.info("Token refresh requested");
            var result = authService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(RefreshTokenResponse.from(result));
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
