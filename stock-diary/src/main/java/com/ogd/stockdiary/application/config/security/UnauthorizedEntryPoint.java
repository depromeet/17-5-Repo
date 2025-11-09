package com.ogd.stockdiary.application.config.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HttpApiResponse<?> errorResponse;

        // JWT 에러 코드 확인
        CodeEnum errorCode = (CodeEnum) request.getAttribute(JwtAuthenticationFilter.JWT_ERROR_CODE);
        if (errorCode != null) {
            errorResponse = HttpApiResponse.fromExceptionMessage(
                errorCode,
                errorCode.getDescription());
        } else {
            errorResponse = HttpApiResponse.fromExceptionMessage(
                CodeEnum.FRS_002,
                CodeEnum.FRS_002.getDescription());
        }

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
