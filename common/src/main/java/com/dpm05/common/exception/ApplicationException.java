package com.dpm05.common.exception;

import com.dpm05.common.common.httpresponse.CodeEnum;
import lombok.Getter;

import java.util.Map;

@Getter
public class ApplicationException extends RuntimeException {
    private final CodeEnum code;
    @Getter
    private final Map<String, Object> data;

    public ApplicationException(CodeEnum code, String message, Map<String, Object> data) {
        super(message);
        this.code = code;
        this.data = data;
    }
    public ApplicationException(CodeEnum code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }
}