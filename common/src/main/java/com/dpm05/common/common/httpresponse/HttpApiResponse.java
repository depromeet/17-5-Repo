package com.dpm05.common.common.httpresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class HttpApiResponse<T> {
    private CodeEnum code;
    private String message;
    T data;

    public static <T> HttpApiResponse of(T data) {
        return HttpApiResponse.builder()
                .code(CodeEnum.RS_001)
                .data(data)
                .message("")
                .build();
    }

    public static HttpApiResponse fromExceptionMessage(String message) {
        return HttpApiResponse.builder()
                .code(CodeEnum.FRS_001)
                .data(null)
                .message(message)
                .build();
    }

    public static HttpApiResponse fromExceptionMessage(CodeEnum code, String message) {
        return HttpApiResponse.builder()
                .code(code)
                .data(null)
                .message(message)
                .build();
    }

    public static HttpApiResponse fromExceptionMessage(String message, CodeEnum code, Map<String, Object> data) {
        return HttpApiResponse.builder()
                .code(CodeEnum.FRS_001)
                .data(data)
                .message(message)
                .build();
    }
}