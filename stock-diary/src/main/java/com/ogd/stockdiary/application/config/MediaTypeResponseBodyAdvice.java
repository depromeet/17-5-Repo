package com.ogd.stockdiary.application.config;

import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import reactor.core.publisher.Flux;

@ControllerAdvice
public class MediaTypeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    if (Flux.class.isAssignableFrom(methodParameter.getParameterType())) {
      return false;
    }
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    //         예외 또는 특정 타입 응답 시 JSON 미디어 타입으로 강제 변경 예시
    if (body instanceof HttpApiResponse) {
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    return body;
  }
}
