package com.ogd.stockdiary.application.config;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.exception.ApplicationException;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity handleApplicationException(ApplicationException e) {
    logger.error(
        "Application Exception occurred. code={}, message={}", e.getCode().name(), e.getMessage());

    if (e.getCode() == CodeEnum.FRS_002) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              HttpApiResponse.fromExceptionMessage(
                  e.getMessage() != null ? e.getMessage() : e.getCode().getDescription(),
                  e.getCode(),
                  e.getData()));
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            HttpApiResponse.fromExceptionMessage(
                e.getMessage() != null ? e.getMessage() : e.getCode().getDescription(),
                e.getCode(),
                e.getData()));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<HttpApiResponse<?>> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException e) {
    logger.error("HandlerExceptionResolver Exception occurred. message={}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(
            HttpApiResponse.fromExceptionMessage(
                CodeEnum.FRS_005.getDescription(), CodeEnum.FRS_005, null));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<HttpApiResponse<?>> handleValidationException(ValidationException e) {
    logger.error("Validation Exception occurred. message={}", e.getMessage(), e);
    String errorMessage =
        (e.getMessage() != null && !e.getMessage().isBlank())
            ? e.getMessage()
            : CodeEnum.FRS_003.getDescription();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpApiResponse.fromExceptionMessage(CodeEnum.FRS_003, errorMessage + " | "));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<HttpApiResponse<?>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    logger.error(
        "MissingServletRequestParameter Exception occurred. parameterName={}, message={}",
        e.getParameterName(),
        e.getMessage(),
        e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpApiResponse.fromExceptionMessage(CodeEnum.FRS_003, e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<HttpApiResponse<?>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    logger.error("MethodArgumentNotValidException occurred. message={}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpApiResponse.fromExceptionMessage(CodeEnum.FRS_003, createMessage(e)));
  }

  private String createMessage(MethodArgumentNotValidException e) {
    FieldError fe = e.getFieldError();
    if (fe != null && fe.getDefaultMessage() != null) {
      return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "알수없는 오류가 발생했습니다.";
    }

    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    String fields =
        fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
    return fields + " 값들이 정확하지 않습니다.";
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleException(Exception e) {
    logger.error("Exception occurred. message={}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            HttpApiResponse.fromExceptionMessage(
                CodeEnum.FRS_004, CodeEnum.FRS_004.getDescription()));
  }
}
