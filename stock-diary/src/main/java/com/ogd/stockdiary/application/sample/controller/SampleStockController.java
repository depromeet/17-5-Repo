package com.ogd.stockdiary.application.sample.controller;

import com.ogd.stockdiary.application.config.SwaggerConfig;
import com.ogd.stockdiary.application.sample.dto.SampleStockCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sample/stocks")
@Tag(name = "Sample Stock", description = "샘플 주식 관리 API")
public class SampleStockController {

  @Operation(
      summary = "주식 매매 기록 생성",
      description = "새로운 주식 매매 기록을 생성합니다.",
      security =
          @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME) // JWT 인증 필요한 엔드포인트인 경우 추가
      )
  @PostMapping
  public ResponseEntity<String> createStock(@RequestBody SampleStockCreateRequest request) {
    // 샘플 로직
    return ResponseEntity.ok("주식 매매 기록이 생성되었습니다: " + request.getStockName());
  }

  @Operation(
      summary = "주식 목록 조회",
      description = "사용자의 주식 매매 기록 목록을 조회합니다.",
      security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME))
  @GetMapping
  public ResponseEntity<String> getStocks(
      @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10")
          int size) {
    return ResponseEntity.ok("주식 목록 조회 완료 (page=" + page + ", size=" + size + ")");
  }

  @Operation(
      summary = "주식 상세 조회",
      description = "특정 주식 매매 기록의 상세 정보를 조회합니다.",
      security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME))
  @GetMapping("/{stockId}")
  public ResponseEntity<String> getStock(
      @Parameter(description = "주식 기록 ID", example = "1") @PathVariable Long stockId) {
    return ResponseEntity.ok("주식 ID " + stockId + " 상세 정보 조회");
  }

  @Operation(summary = "공개 주식 정보 조회", description = "인증 없이 접근 가능한 공개 주식 정보를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @GetMapping("/public")
  public ResponseEntity<String> getPublicStockInfo() {
    return ResponseEntity.ok("공개 주식 정보입니다. 인증이 필요없습니다.");
  }
}
