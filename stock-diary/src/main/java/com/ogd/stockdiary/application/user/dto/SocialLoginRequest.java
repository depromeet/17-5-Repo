package com.ogd.stockdiary.application.user.dto;

import com.ogd.stockdiary.domain.user.entity.OAuthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 요청")
public class SocialLoginRequest {

    @Schema(description = "OAuth Provider", example = "KAKAO", allowableValues = {"KAKAO", "APPLE"})
    private OAuthProvider provider;

    @Schema(description = "OAuth Authorization Code (SDK로부터 받은 인증 코드)", example = "AQBxxx...", required = true)
    private String authCode;

    @Schema(description = """
        OAuth authCode 발급 시 사용한 redirect_uri

        - Android SDK 사용 시: SDK가 사용한 redirect_uri를 반드시 전달 (예: kakao{APP_KEY}://oauth)
        - null인 경우: 서버 설정(application.yml)의 redirect_uri 사용 (웹 callback URL)
        - 주의: authCode 획득 시 사용한 redirect_uri와 동일해야 함 (OAuth 2.0 보안 요구사항)
        """, example = "kakao123456://oauth", nullable = true)
    private String redirectUri;

    @Schema(description = """
        사용자 이메일 (선택적)

        - Kakao: null 전달 가능 (idToken payload에서 자동 추출)
        - Apple 첫 로그인: SDK가 제공한 값을 반드시 전달 (Apple은 첫 로그인 시에만 제공)
        - Apple 재로그인: null 전달 가능 (서버 DB에 저장된 값 사용)
        """, example = "user@example.com", nullable = true)
    private String email;

    @Schema(description = """
        사용자 닉네임 (선택적)

        - Kakao: null 전달 가능 (idToken payload에서 자동 추출)
        - Apple 첫 로그인: SDK가 제공한 값을 반드시 전달 (Apple은 첫 로그인 시에만 제공)
        - Apple 재로그인: null 전달 가능 (서버 DB에 저장된 값 사용)
        """, example = "홍길동", nullable = true)
    private String nickname;
}
