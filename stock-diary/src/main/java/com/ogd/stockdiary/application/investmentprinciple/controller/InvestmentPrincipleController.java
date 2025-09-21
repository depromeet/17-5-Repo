package com.ogd.stockdiary.application.investmentprinciple.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.ogd.stockdiary.application.investmentprinciple.dto.mapper.InvestmentPrincipleMapper;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.BatchProcessRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.CreateMultiplePrinciplesRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.CreatePrincipleRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.UpdatePrincipleRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.response.BatchProcessResponse;
import com.ogd.stockdiary.application.investmentprinciple.dto.response.InvestmentPrincipleResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessResult;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.usecase.InvestmentPrincipleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/investment-principles")
@RequiredArgsConstructor
@Tag(name = "Investment Principle", description = "투자원칙 관리 API")
public class InvestmentPrincipleController {

  private final InvestmentPrincipleUseCase investmentPrincipleUseCase;

  @GetMapping
  @Operation(summary = "투자원칙 목록 조회", description = "사용자의 모든 투자원칙을 조회합니다.")
  public ResponseEntity<HttpApiResponse<List<InvestmentPrincipleResponse>>> getUserPrinciples() {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    List<InvestmentPrinciple> principles = investmentPrincipleUseCase.getUserPrinciples(userId);
    List<InvestmentPrincipleResponse> responses = InvestmentPrincipleMapper.toResponseList(principles);

    return ResponseEntity.ok(HttpApiResponse.of(responses));
  }

  @PostMapping
  @Operation(summary = "투자원칙 생성", description = "새로운 투자원칙을 생성합니다.")
  public ResponseEntity<HttpApiResponse<InvestmentPrincipleResponse>> createPrinciple(
      @Valid @RequestBody CreatePrincipleRequest request) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    CreatePrincipleCommand command = InvestmentPrincipleMapper.toCommand(request, userId);
    InvestmentPrinciple principle = investmentPrincipleUseCase.createPrinciple(command);
    InvestmentPrincipleResponse response = InvestmentPrincipleMapper.toResponse(principle);

    return ResponseEntity.status(HttpStatus.CREATED).body(HttpApiResponse.of(response));
  }

  @PostMapping("/multiple")
  @Operation(summary = "다중 투자원칙 생성", description = "여러 투자원칙을 한번에 생성합니다.")
  public ResponseEntity<HttpApiResponse<List<InvestmentPrincipleResponse>>> createMultiplePrinciples(
      @Valid @RequestBody CreateMultiplePrinciplesRequest request) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    CreateMultiplePrinciplesCommand command = InvestmentPrincipleMapper.toCommand(request, userId);
    List<InvestmentPrinciple> principles = investmentPrincipleUseCase.createMultiplePrinciples(command);
    List<InvestmentPrincipleResponse> responses = InvestmentPrincipleMapper.toResponseList(principles);

    return ResponseEntity.status(HttpStatus.CREATED).body(HttpApiResponse.of(responses));
  }

  @PutMapping("/{principleId}")
  @Operation(summary = "투자원칙 수정", description = "투자원칙의 내용을 수정합니다.")
  public ResponseEntity<HttpApiResponse<InvestmentPrincipleResponse>> updatePrinciple(@PathVariable Long principleId,
      @Valid @RequestBody UpdatePrincipleRequest request) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    UpdatePrincipleCommand command = InvestmentPrincipleMapper.toCommand(request, principleId, userId);
    InvestmentPrinciple principle = investmentPrincipleUseCase.updatePrinciple(command);
    InvestmentPrincipleResponse response = InvestmentPrincipleMapper.toResponse(principle);

    return ResponseEntity.ok(HttpApiResponse.of(response));
  }

  @DeleteMapping("/{principleId}")
  @Operation(summary = "투자원칙 삭제", description = "투자원칙을 삭제합니다.")
  public ResponseEntity<HttpApiResponse<Void>> deletePrinciple(@PathVariable Long principleId) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    investmentPrincipleUseCase.deletePrinciple(principleId, userId);

    return ResponseEntity.ok(HttpApiResponse.of(null));
  }

  @PostMapping("/batch")
  @Operation(summary = "투자원칙 일괄 처리", description = "투자원칙을 생성/수정/삭제를 한번에 처리합니다.")
  public ResponseEntity<HttpApiResponse<BatchProcessResponse>> batchProcessPrinciples(
      @Valid @RequestBody BatchProcessRequest request) {

    // TODO: Spring Security에서 User 정보 가져오기
    Long userId = 1L; // 임시로 하드코딩

    BatchProcessCommand command = InvestmentPrincipleMapper.toCommand(request, userId);
    BatchProcessResult result = investmentPrincipleUseCase.batchProcessPrinciples(command);
    BatchProcessResponse response = InvestmentPrincipleMapper.toBatchResponse(result);

    return ResponseEntity.ok(HttpApiResponse.of(response));
  }
}
