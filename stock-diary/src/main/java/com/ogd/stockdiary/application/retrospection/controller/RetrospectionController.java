package com.ogd.stockdiary.application.retrospection.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.retrospection.dto.request.CreateMemoRequest;
import com.ogd.stockdiary.application.retrospection.dto.request.CreateRetrospectionRequest;
import com.ogd.stockdiary.application.retrospection.dto.request.UpdateMemoRequest;
import com.ogd.stockdiary.application.retrospection.dto.response.CreateRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.MarketGroupResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.*;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/retrospections")
@RequiredArgsConstructor
@Tag(name = "Retrospection", description = "회고 관리 API")
public class RetrospectionController {

    private final CreateRetrospectionUseCase createRetrospectionUseCase;
    private final GetRetrospectionUseCase getRetrospectionUseCase;
    private final DeleteRetrospectionUseCase deleteRetrospectionUseCase;
    private final CreateMemoUseCase createMemoUseCase;
    private final UpdateMemoUseCase updateMemoUseCase;
    private final DeleteMemoUseCase deleteMemoUseCase;

    @PostMapping
    @Operation(summary = "회고 생성", description = "주식 거래 회고를 생성합니다.")
    public HttpApiResponse<CreateRetrospectionResponse> createRetrospection(
        @Valid @RequestBody CreateRetrospectionRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        CreateRetrospectionCommand command = RetrospectionMapper.toCommand(request, userId);
        Retrospection retrospection = createRetrospectionUseCase.createRetrospection(command);
        CreateRetrospectionResponse response = RetrospectionMapper.toResponse(retrospection);

        return HttpApiResponse.of(response);
    }

    @GetMapping("/{retrospectionId}")
    @Operation(summary = "회고 조회", description = "특정 회고를 조회합니다.")
    public HttpApiResponse<GetRetrospectionResponse> getRetrospection(
        @PathVariable Long retrospectionId) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        GetRetrospectionResponse response = getRetrospectionUseCase.getRetrospection(retrospectionId, userId);

        return HttpApiResponse.of(response);
    }

    @GetMapping()
    public HttpApiResponse<List<MarketGroupResponse>> getAllRetrospcetions() {
        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L;

        GetRetrospectionCommand command = RetrospectionMapper.toCommand(userId);

        List<MarketGroupResponse> response = getRetrospectionUseCase.getAllRetrospections(command);

        return HttpApiResponse.of(response);

    }

    @DeleteMapping("/{retrospectionId}")
    @Operation(summary = "회고 삭제", description = "회고 id로 회고를 삭제합니다.")
    public HttpApiResponse<String> delete(@PathVariable Long retrospectionId) {
        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L;

        deleteRetrospectionUseCase.deleteRetrospection(retrospectionId);

        return HttpApiResponse.of("success");
    }

    // Memo CRUD endpoints

    @PostMapping("/{retrospectionId}/memos")
    @Operation(summary = "메모 생성", description = "회고에 메모를 추가합니다.")
    public HttpApiResponse<String> createMemo(
        @PathVariable Long retrospectionId,
        @Valid @RequestBody CreateMemoRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        createMemoUseCase.createMemo(retrospectionId, request.getContent(), userId);

        return HttpApiResponse.of("success");
    }

    @PutMapping("/{retrospectionId}/memos/{memoId}")
    @Operation(summary = "메모 수정", description = "메모 내용을 수정합니다.")
    public HttpApiResponse<String> updateMemo(
        @PathVariable Long retrospectionId,
        @PathVariable Long memoId,
        @Valid @RequestBody UpdateMemoRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        updateMemoUseCase.updateMemo(retrospectionId, memoId, request.getContent(), userId);

        return HttpApiResponse.of("success");
    }

    @DeleteMapping("/{retrospectionId}/memos/{memoId}")
    @Operation(summary = "메모 삭제", description = "메모를 삭제합니다.")
    public HttpApiResponse<String> deleteMemo(
        @PathVariable Long retrospectionId,
        @PathVariable Long memoId) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        deleteMemoUseCase.deleteMemo(retrospectionId, memoId, userId);

        return HttpApiResponse.of("success");
    }
}
