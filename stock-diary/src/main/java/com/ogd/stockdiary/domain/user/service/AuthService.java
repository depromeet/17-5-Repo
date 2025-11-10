package com.ogd.stockdiary.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogd.stockdiary.application.user.repository.AppleAuthTokenRepository;
import com.ogd.stockdiary.application.user.repository.RefreshTokenRepository;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
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
import com.ogd.stockdiary.exception.ApplicationException;

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
        OAuthProvider provider, String authCode, String idToken, String redirectUri, String email, String nickname) {
        OAuthClient client = oAuthClientFactory.getClient(provider);

        OAuthTokenResponse tokenResponse;

        // 1. authCode 또는 idToken으로 토큰 획득
        if (idToken != null && !idToken.isEmpty()) {
            // iOS SDK 방식: idToken 직접 사용
            tokenResponse = client.getTokenFromIdToken(idToken);
        } else if (authCode != null && !authCode.isEmpty()) {
            // 웹/Android 방식: authCode로 토큰 교환
            tokenResponse = client.getToken(authCode, redirectUri);
        } else {
            throw new IllegalArgumentException("authCode 또는 idToken 중 하나는 필수입니다");
        }

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

        // 6. Apple OAuth Refresh Token 저장 (연결 해제 시 사용)
        // Apple이 발급한 refresh token을 저장 (unlink API 호출 시 필요)
        if (provider == OAuthProvider.APPLE && tokenResponse.getRefreshToken() != null) {
            saveOrUpdateAppleRefreshToken(user.getId(), tokenResponse.getRefreshToken());
        }

        // 7. 우리 서비스 JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 8. 우리 서비스 JWT Refresh Token 저장 (토큰 갱신 시 사용)
        saveOrUpdateRefreshToken(user.getId(), refreshToken);

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

    private AppleAuthToken saveOrUpdateAppleRefreshToken(Long userId, String refreshToken) {
        return appleAuthTokenRepository.findById(userId)
            .map(existingToken -> {
                existingToken.updateRefreshToken(refreshToken);
                return existingToken;
            })
            .orElseGet(() -> appleAuthTokenRepository.save(new AppleAuthToken(userId, refreshToken)));
    }

    private RefreshToken saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        LocalDateTime expiresAt = LocalDateTime.now()
            .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        return refreshTokenRepository.findById(userId)
            .map(existingToken -> {
                existingToken.updateToken(refreshToken, expiresAt);
                return existingToken;
            })
            .orElseGet(() -> refreshTokenRepository.save(new RefreshToken(userId, refreshToken, expiresAt)));
    }

    @Transactional
    public TokenRefreshResult refreshAccessToken(String refreshToken) {
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
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        // 7. 새로운 Refresh Token 생성 (Refresh Token Rotation)
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // 8. DB의 Refresh Token 업데이트
        saveOrUpdateRefreshToken(userId, newRefreshToken);

        log.info("Token refreshed for userId: {}", userId);

        return new TokenRefreshResult(newAccessToken, newRefreshToken);
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

    @Transactional
    public void withdrawUser(Long userId, String authCode) {
        User user = userRepository
            .findById(userId)
                .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003, "User not found"));

        OAuthProvider provider = user.getOAuthProviderInfo().getOauthProvider();

        // Provider별 소셜 연결 해제
        if (provider == OAuthProvider.APPLE) {
            // Apple: authCode 필수
            if (authCode == null || authCode.isEmpty()) {
                throw new ApplicationException(
                    CodeEnum.FRS_005,
                    "Apple 회원탈퇴 시 authCode는 필수입니다",
                    null);
            }

            OAuthClient client = oAuthClientFactory.getClient(OAuthProvider.APPLE);
            client.unlink(authCode);

            // AppleAuthToken이 있으면 삭제
            appleAuthTokenRepository.findById(userId)
                .ifPresent(appleAuthTokenRepository::delete);

        } else if (provider == OAuthProvider.KAKAO) {
            // Kakao: subject로 연결 해제
            OAuthClient client = oAuthClientFactory.getClient(OAuthProvider.KAKAO);
            client.unlink(user.getOAuthProviderInfo().getSubject());
        }
        // GOOGLE 등 기타 provider는 소셜 API 호출 없이 DB만 처리

        // JWT Refresh Token 삭제
        refreshTokenRepository.deleteById(userId);

        // Soft Delete
        user.setIsDeleted(true);
        userRepository.save(user);

        log.info("User withdrawn successfully - userId: {}, provider: {}", userId, provider);
    }
}
