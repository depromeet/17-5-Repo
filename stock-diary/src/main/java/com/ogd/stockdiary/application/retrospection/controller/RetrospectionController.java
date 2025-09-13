package com.ogd.stockdiary.application.retrospection.controller;

import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.retrospection.dto.request.CreateRetrospectionRequest;
import com.ogd.stockdiary.application.retrospection.dto.response.CreateRetrospectionResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/retrospections")
@RequiredArgsConstructor
@Tag(name = "Retrospection", description = "회고 관리 API")
public class RetrospectionController {

  private final CreateRetrospectionUseCase createRetrospectionUseCase;

  @PostMapping
  @Operation(summary = "회고 생성", description = "주식 거래 회고를 생성합니다.")
  public ResponseEntity<HttpApiResponse<CreateRetrospectionResponse>> createRetrospection(
      @RequestHeader("Authorization_code") String authorizationCode,
      @Valid @RequestBody CreateRetrospectionRequest request) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    CreateRetrospectionCommand command = RetrospectionMapper.toCommand(request, userId);
    Retrospection retrospection = createRetrospectionUseCase.createRetrospection(command);
    CreateRetrospectionResponse response = RetrospectionMapper.toResponse(retrospection);

    return ResponseEntity.status(HttpStatus.CREATED).body(HttpApiResponse.of(response));
  }
}
