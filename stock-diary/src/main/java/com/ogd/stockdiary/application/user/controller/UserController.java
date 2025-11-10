package com.ogd.stockdiary.application.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.application.config.security.AuthenticatedUser;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.user.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자", description = "사용자 관련 API (인증 필요)")
public class UserController {

    private final AuthService authService;

    @Operation(summary = "회원탈퇴", description = """
        ## 회원탈퇴 API

        현재 로그인한 사용자의 회원탈퇴를 처리합니다.

        ### 처리 과정
        1. JWT 토큰에서 사용자 정보 추출
        2. 소셜 플랫폼 연결 해제 (Apple, Kakao)
        3. JWT Refresh Token 삭제
        4. 사용자 계정 비활성화 (Soft Delete)

        ### 인증
        - Authorization 헤더에 Bearer 토큰 필요
        - 예: `Authorization: Bearer eyJhbGc...`

        ### 파라미터
        - authCode: Apple 사용자의 경우 필수, 기타 Provider는 선택

        ### 주의사항
        - Apple: authCode를 사용하여 Apple 토큰 취소 (필수)
        - Kakao: 카카오 연결 해제 API 호출
        - Google: DB만 처리 (소셜 API 호출 없음)
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
        @ApiResponse(responseCode = "400", description = "회원탈퇴 실패", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping
    public HttpApiResponse<Void> withdraw(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) String authCode) {
        log.info("User withdrawal requested - userId: {}, authCode provided: {}",
            authenticatedUser.getUserId(), authCode != null);
        authService.withdrawUser(authenticatedUser.getUserId(), authCode);
        return HttpApiResponse.ok();
    }
}
