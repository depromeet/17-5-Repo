# 소셜 로그인 아키텍처 상세 구현 문서

## 1. 아키텍처 개요

현재 프로젝트의 소셜 로그인은 **Strategy Pattern**과 **Factory Pattern**을 활용한 확장 가능한 구조로 설계되어 있습니다. 각 OAuth Provider(카카오, 애플, 구글)에 대해 공통 인터페이스를 구현하여 확장성과 유지보수성을 보장합니다.

## 2. 핵심 구조

### 2.1 추상화 레이어

#### OAuthClient Interface (`OAuthClient.kt`)
```kotlin
interface OAuthClient {
    fun getToken(authCode: String): OAuthTokenResponse
    fun getPublicKeys(): OIDCPublicKeyList
    fun unlink(identifier: String)
}
```

**역할:**
- 모든 OAuth Provider에 대한 공통 인터페이스 정의
- `getToken`: 인가 코드로 ID 토큰 획득
- `getPublicKeys`: ID 토큰 검증용 공개키 목록 조회
- `unlink`: 소셜 계정 연결 해제

#### OAuthClientFactory (`OAuthClientFactory.kt`)
```kotlin
@Component
class OAuthClientFactory(
    private val kakaoOAuthClient: KakaoOAuthClient,
    private val appleOAuthClient: AppleOAuthClient,
) {
    fun getClient(provider: OAuthProvider): OAuthClient {
        return when (provider) {
            OAuthProvider.KAKAO -> kakaoOAuthClient
            OAuthProvider.GOOGLE -> TODO()
            OAuthProvider.APPLE -> appleOAuthClient
        }
    }
}
```

**역할:**
- Factory Pattern 구현
- Provider 타입에 따른 적절한 OAuthClient 인스턴스 반환
- 새로운 Provider 추가 시 이 클래스만 수정하면 됨

### 2.2 Provider 구현체

#### KakaoOAuthClient (`KakaoOAuthClient.kt`)
- **Feign Client**: `KakaoOAuthFeignClient`, `KakaoFeignClient`
- **특징**:
    - OIDC 표준 엔드포인트 사용 (`/.well-known/jwks.json`)
    - Admin Key를 사용한 계정 연결 해제
    - 로컬 캐싱 적용 (`@Cacheable`)

#### AppleOAuthClient (`AppleOAuthClient.kt`)
- **Feign Client**: `AppleFeignClient`
- **특징**:
    - JWT 형식 Client Secret 동적 생성 (ES256 알고리즘)
    - Private Key를 사용한 JWT 서명
    - Apple Refresh Token DB 저장/관리
    - 계정 탈퇴 시 Apple API를 통한 연결 해제

## 3. ID 토큰 검증 시스템

### OIDC Token Verification (`OIDCTokenVerification.kt`)

```kotlin
@Component
class OIDCTokenVerification {
    fun verifyIdToken(idToken: String, oidcPublicKeys: OIDCPublicKeyList): OIDCPayload
}
```

**검증 프로세스:**
1. ID 토큰 헤더에서 `kid`, `alg` 추출
2. Provider 공개키 목록에서 매칭되는 키 찾기
3. RSA 공개키 생성
4. JWT 서명 검증 및 페이로드 추출
5. 예외 처리 (만료, 서명 오류, 형식 오류 등)

### 추출 데이터 (`OIDCPayload.kt`)
```kotlin
data class OIDCPayload(
    val subject: String,      // OAuth Subject (고유 식별자)
    val email: String?,       // 이메일
    val picture: String?,     // 프로필 이미지
    val name: String?,        // 닉네임
)
```

## 4. 인증 플로우

### 4.1 공통 로그인 플로우 (`AuthService.kt`)

1. **토큰 획득**: 인가 코드로 ID 토큰 요청
2. **공개키 조회**: 토큰 검증용 공개키 목록 조회
3. **토큰 검증**: ID 토큰 서명 검증 및 페이로드 추출
4. **회원 조회/생성**: 기존 회원 조회 또는 신규 회원 자동 생성
5. **JWT 발급**: 내부 시스템용 JWT Access/Refresh 토큰 생성
6. **토큰 저장**: Redis에 Refresh 토큰 저장

### 4.2 애플 특별 처리

애플의 경우 추가적인 처리가 필요:
- **Apple Refresh Token 저장**: 계정 탈퇴 시 Apple API 호출용
- **Client Secret 동적 생성**: 요청 시마다 JWT 형식으로 생성
- **이메일/닉네임 별도 처리**: 클라이언트에서 제공받은 정보 사용

## 5. 데이터 모델

### Member Domain (`Member.kt`)
```kotlin
class Member(
    val id: Long?,
    var nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val oAuthProviderInfo: OAuthProviderInfo,
    // ...
)
```

### OAuth Provider 정보 (`OAuthProviderInfo.kt`)
```kotlin
class OAuthProviderInfo(
    val oauthProvider: OAuthProvider,
    val subject: String,  // OAuth Subject
)
```

### Apple Auth Token (`AppleAuthToken.kt`)
```kotlin
class AppleAuthToken(
    val memberId: Long,
    var refreshToken: String,
)
```

## 6. 설정 및 Properties

### Apple Properties (`AppleProperties.kt`)
```kotlin
@ConfigurationProperties(prefix = "apple")
data class AppleProperties(
    val clientId: String,
    val redirectUri: String,
    val aud: String,
    val teamId: String,
    val keyId: String,
    val privateKey: String,  // Base64 인코딩된 Private Key
)
```

## 7. Java 프로젝트 적용 가이드

### 7.1 패키지 구조
```
com.example.project.auth/
├── client/
│   ├── OAuthClient.java (interface)
│   ├── OAuthClientFactory.java
│   ├── AppleOAuthClient.java
│   └── feign/
│       └── AppleFeignClient.java
├── oidc/
│   ├── OIDCTokenVerification.java
│   ├── OIDCPayload.java
│   └── OIDCPublicKey.java
├── properties/
│   └── AppleProperties.java
└── service/
    └── AuthService.java
```

### 7.2 핵심 인터페이스 (Java 버전)
```java
public interface OAuthClient {
    OAuthTokenResponse getToken(String authCode);
    OIDCPublicKeyList getPublicKeys();
    void unlink(String identifier);
}
```

### 7.3 Factory Pattern 구현
```java
@Component
public class OAuthClientFactory {
    private final AppleOAuthClient appleOAuthClient;
    
    public OAuthClient getClient(OAuthProvider provider) {
        return switch (provider) {
            case APPLE -> appleOAuthClient;
            case GOOGLE -> throw new UnsupportedOperationException("Google not implemented yet");
        };
    }
}
```

### 7.4 Apple Client 구현 포인트

1. **JWT Client Secret 생성**:
    - `io.jsonwebtoken.Jwts` 라이브러리 사용
    - ES256 알고리즘으로 서명
    - BouncyCastle을 사용한 Private Key 처리

2. **Feign Client 설정**:
    - `@FeignClient(url = "https://appleid.apple.com")`
    - 로컬 캐시 설정: `@Cacheable(value = "oidcPublicKeys", key = "'apple'")`

3. **ID 토큰 검증**:
    - RSA 공개키 생성 및 JWT 검증
    - 예외 처리 (ExpiredJwtException, SignatureException 등)

### 7.5 로컬 캐시 설정

기존 Redis 캐시 대신 Spring의 로컬 캐시를 사용:

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

Feign Client에서 캐시 적용:
```java
@GetMapping("/auth/keys")
@Cacheable(value = "oidcPublicKeys", key = "'apple'")
OIDCPublicKeyList getPublicKeys();
```

### 7.6 필요한 의존성 (Maven)
```xml
<dependencies>
    <!-- JWT 처리 -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- BouncyCastle (Apple Private Key 처리) -->
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcpkix-jdk15on</artifactId>
        <version>1.70</version>
    </dependency>
    
    <!-- Feign Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- 로컬 캐시 (Caffeine) -->
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
</dependencies>
```

## 8. 확장성 고려사항

1. **새로운 Provider 추가**:
    - `OAuthProvider` enum에 추가
    - 해당 Provider의 `OAuthClient` 구현체 작성
    - `OAuthClientFactory`에 매핑 추가

2. **공통 로직 활용**:
    - OIDC 토큰 검증 로직 재사용
    - 공통 예외 처리
    - 로컬 캐싱 전략 공유

3. **설정 외부화**:
    - Properties 파일을 통한 Provider별 설정 관리
    - 환경별 설정 분리 (dev, prod)

4. **캐시 전략**:
    - 로컬 캐시 사용으로 Redis 의존성 제거
    - 공개키 조회 응답 시간 최적화
    - TTL 설정을 통한 보안 강화

## 9. 애플 로그인 구현 우선순위

Java 프로젝트에서 애플 로그인을 우선 구현할 때의 단계별 접근:

1. **Phase 1**: 기본 구조 설정
    - `OAuthClient` 인터페이스 정의
    - `AppleOAuthClient` 구현 
    - feign client가 아닌 rest client로 구현
    - Apple Properties 설정

2. **Phase 2**: 토큰 처리
    - JWT Client Secret 생성 로직
    - OIDC 토큰 검증 시스템
    - Apple Feign Client 구현

3. **Phase 3**: 통합 및 테스트
    - AuthService 통합
    - 로컬 캐시 적용
    - 예외 처리 및 로깅

4. **Phase 4**: 확장 준비
    - Factory Pattern 적용
    - 다른 Provider 추가를 위한 구조 완성

