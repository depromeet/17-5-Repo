package com.ogd.stockdiary.domain.user.service;

import com.ogd.stockdiary.domain.user.entity.AppleAuthToken;
import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.entity.OAuthProviderInfo;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.domain.user.port.out.oauth.OAuthTokenResponse;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPayload;
import com.ogd.stockdiary.domain.user.port.out.oauth.OIDCPublicKeyList;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClient;
import com.ogd.stockdiary.domain.user.port.out.oauth.client.OAuthClientFactory;
import com.ogd.stockdiary.application.user.repository.AppleAuthTokenRepository;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final OAuthClientFactory oAuthClientFactory;
    private final OIDCTokenVerification oidcTokenVerification;
    private final UserRepository userRepository;
    private final AppleAuthTokenRepository appleAuthTokenRepository;
    
    @Transactional
    public AuthResult socialLogin(OAuthProvider provider, String authCode, String email, String nickname) {
        OAuthClient client = oAuthClientFactory.getClient(provider);
        
        // 1. 토큰 획득
        OAuthTokenResponse tokenResponse = client.getToken(authCode);
        
        // 2. 공개키 조회
        OIDCPublicKeyList publicKeys = client.getPublicKeys();
        
        // 3. ID 토큰 검증
        OIDCPayload payload = oidcTokenVerification.verifyIdToken(tokenResponse.getIdToken(), publicKeys);
        
        // 4. 기존 사용자 확인
        boolean isNewUser = !userRepository.findByOAuthProviderAndSubject(provider, payload.getSubject()).isPresent();
        
        // 5. 회원 조회 또는 생성
        User user = userRepository.findByOAuthProviderAndSubject(provider, payload.getSubject())
            .orElseGet(() -> createNewUser(provider, payload, email, nickname));
        
        // 6. Apple의 경우 refresh token 저장
        if (provider == OAuthProvider.APPLE && tokenResponse.getRefreshToken() != null) {
            saveAppleRefreshToken(user.getId(), tokenResponse.getRefreshToken());
        }
        
        return new AuthResult(user, isNewUser);
    }
    
    private User createNewUser(OAuthProvider provider, OIDCPayload payload, String providedEmail, String providedNickname) {
        String email = payload.getEmail() != null ? payload.getEmail() : providedEmail;
        String nickname = payload.getName() != null ? payload.getName() : providedNickname;
        
        if (email == null) {
            throw new IllegalArgumentException("Email is required for user registration");
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
    public void unlinkSocialAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        OAuthProvider provider = user.getOAuthProviderInfo().getOauthProvider();
        OAuthClient client = oAuthClientFactory.getClient(provider);
        
        if (provider == OAuthProvider.APPLE) {
            // Apple의 경우 저장된 refresh token으로 연결 해제
            AppleAuthToken appleAuthToken = appleAuthTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Apple auth token not found"));
            
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