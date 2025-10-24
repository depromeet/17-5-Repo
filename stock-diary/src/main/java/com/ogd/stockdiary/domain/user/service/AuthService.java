package com.ogd.stockdiary.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogd.stockdiary.application.user.repository.AppleAuthTokenRepository;
import com.ogd.stockdiary.application.user.repository.RefreshTokenRepository;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.domain.user.config.JwtProperties;
import com.ogd.stockdiary.domain.user.entity.AppleAuthToken;
import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.entity.OAuthProviderInfo;
import com.ogd.stockdiary.domain.user.entity.RefreshToken;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.domain.user.port.out.oauth.OAuthTokenResponse;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPayload;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClient;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClientFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final OAuthClientFactory oAuthClientFactory;
    private final OIDCTokenVerification oidcTokenVerification;
    private final UserRepository userRepository;
    private final AppleAuthTokenRepository appleAuthTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResult socialLogin(
        OAuthProvider provider, String authCode, String redirectUri, String email, String nickname) {
        OAuthClient client = oAuthClientFactory.getClient(provider);

        // 1. 토큰 획득 (redirectUri가 null이면 OAuthClient가 기본값 사용)
        OAuthTokenResponse tokenResponse = client.getToken(authCode, redirectUri);

        // 2. 공개키 조회
        OIDCPublicKeyList publicKeys = client.getPublicKeys();

        // 3. ID 토큰 검증
        OIDCPayload payload = oidcTokenVerification.verifyIdToken(tokenResponse.getIdToken(), publicKeys);

        // 4. 기존 사용자 확인
        boolean isNewUser = !userRepository
            .findByOAuthProviderAndSubject(provider, payload.getSubject())
            .isPresent();

        // 5. 회원 조회 또는 생성
        User user = userRepository
            .findByOAuthProviderAndSubject(provider, payload.getSubject())
            .orElseGet(() -> createNewUser(provider, payload, email, nickname));

        // 6. Apple의 경우 refresh token 저장
        if (provider == OAuthProvider.APPLE && tokenResponse.getRefreshToken() != null) {
            saveAppleRefreshToken(user.getId(), tokenResponse.getRefreshToken());
        }

        // 7. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 8. Refresh Token 저장 또는 업데이트
        LocalDateTime expiresAt = LocalDateTime.now()
            .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);
        refreshTokenRepository.findById(user.getId())
            .ifPresentOrElse(
                existingToken -> existingToken.updateToken(refreshToken, expiresAt),
                () -> refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken, expiresAt)));

        return new AuthResult(user, isNewUser, accessToken, refreshToken);
    }

    private User createNewUser(
        OAuthProvider provider,
        OIDCPayload payload,
        String providedEmail,
        String providedNickname) {
        String email = payload.getEmail() != null ? payload.getEmail() : providedEmail;
        String nickname = payload.getName() != null ? payload.getName() : providedNickname;

        // 이메일이 없으면 임시 이메일 생성 (테스트용)
        if (email == null) {
            email = provider.name().toLowerCase() + "_" + payload.getSubject() + "@temporary.com";
            log.warn("Email not provided, using temporary email: {}", email);
        }

        if (nickname == null) {
            nickname = "User" + System.currentTimeMillis(); // 기본 닉네임 생성
        }

        OAuthProviderInfo providerInfo = new OAuthProviderInfo(provider, payload.getSubject());
        User newUser = new User(nickname, email, payload.getPicture(), providerInfo);

        return userRepository.save(newUser);
    }

    private void saveAppleRefreshToken(Long userId, String refreshToken) {
        AppleAuthToken appleAuthToken = new AppleAuthToken(userId, refreshToken);
        appleAuthTokenRepository.save(appleAuthToken);
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 2. 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. DB에서 Refresh Token 확인
        RefreshToken storedToken = refreshTokenRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        // 4. DB의 토큰과 일치하는지 확인
        if (!storedToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        // 5. 만료 여부 확인
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        // 6. 새로운 Access Token 생성
        return jwtTokenProvider.generateAccessToken(userId);
    }

    @Transactional
    public void unlinkSocialAccount(Long userId) {
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OAuthProvider provider = user.getOAuthProviderInfo().getOauthProvider();
        OAuthClient client = oAuthClientFactory.getClient(provider);

        if (provider == OAuthProvider.APPLE) {
            // Apple의 경우 저장된 refresh token으로 연결 해제
            AppleAuthToken appleAuthToken = appleAuthTokenRepository
                .findById(userId)
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        "Apple auth token not found"));

            client.unlink(appleAuthToken.getRefreshToken());
            appleAuthTokenRepository.delete(appleAuthToken);
        } else {
            // 다른 provider의 경우 subject 사용
            client.unlink(user.getOAuthProviderInfo().getSubject());
        }

        // 사용자 삭제 또는 비활성화
        user.setIsDeleted(true);
        userRepository.save(user);
    }
}
