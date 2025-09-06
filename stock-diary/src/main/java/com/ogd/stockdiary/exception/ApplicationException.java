package com.ogd.stockdiary.exception;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import java.util.Map;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
  private final CodeEnum code;
  @Getter private final Map<String, Object> data;

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
