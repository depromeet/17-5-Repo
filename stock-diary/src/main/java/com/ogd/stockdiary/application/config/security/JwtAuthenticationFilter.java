package com.ogd.stockdiary.application.config.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.user.service.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    // UnauthorizedEntryPoint에서 어떤 오류를 발생했는지 attritbute로 파악하기 위한 키값 임.
    // 더 나은 방식으로 오류코드를 전달하고 싶으나 추후 아이디어가 있으신 분은 개선 바람.
    public static final String JWT_ERROR_CODE = "jwtErrorCode";

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/v1/auth/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/favicon.ico",
        "/error");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        log.info("[JwtFilter] Processing request: {}, Authorization header: {}",
            request.getRequestURI(),
            authorizationHeader != null ? "Bearer ***" : "null");

        Long userId = null;
        boolean hasToken = authorizationHeader != null && authorizationHeader.startsWith("Bearer ");

        if (hasToken) {
            String token = authorizationHeader.substring(7);
            log.info("[JwtFilter] Token found, attempting to extract userId");

            try {
                userId = jwtTokenProvider.getUserIdFromToken(token);
                log.info("[JwtFilter] Successfully extracted userId: {}", userId);
            } catch (ExpiredJwtException e) {
                // JWT 토큰 만료 - 직접 응답 작성
                log.warn("JWT token expired: {}", e.getMessage());
                sendErrorResponse(response, CodeEnum.ACCESS_TOKEN_EXPIRED);
                return;
            } catch (Exception e) {
                // 기타 JWT 검증 실패 - 직접 응답 작성
                log.error("JWT validation failed: {}", e.getMessage());
                sendErrorResponse(response, CodeEnum.FRS_002);
                return;
            }
        }

        // TODO: 개발 편의를 위해 토큰이 아예 없을 경우만 userId=1 사용. 프로덕션에서는 제거 필요
        if (userId == null && !hasToken) {
            userId = 1L;
            log.debug("No Authorization header found. Using default userId=1 for development");
        }

        // userId가 있을 때만 SecurityContext 설정
        if (userId != null) {
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, CodeEnum errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HttpApiResponse<?> errorResponse = HttpApiResponse.fromExceptionMessage(
            errorCode,
            errorCode.getDescription());

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
