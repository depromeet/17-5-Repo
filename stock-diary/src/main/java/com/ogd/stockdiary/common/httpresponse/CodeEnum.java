package com.ogd.stockdiary.common.httpresponse;

public enum CodeEnum {
    RS_001("실행 성공"), FRS_001("실행 실패"), FRS_002("권한 없음"), FRS_003("데이터 없음"), FRS_004("서버 오류"), FRS_005("잘못된 요청"),

    // OAuth 관련 에러
    AUTH_001("OAuth 토큰 교환 실패"), AUTH_002("OAuth 토큰 검증 실패"), AUTH_003("Refresh Token 만료"), AUTH_004("잘못된 Refresh Token"),

    // JWT 토큰 관련 에러
    ACCESS_TOKEN_EXPIRED("Access Token 만료"),
    ;

    private final String description;

    public String getDescription() {
        return description;
    }

    CodeEnum(String description) {
        this.description = description;
    }
}
