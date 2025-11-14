package com.ogd.stockdiary.application.principlegroup.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.application.config.security.AuthenticatedUser;
import com.ogd.stockdiary.application.principlegroup.dto.mapper.PrincipleGroupMapper;
import com.ogd.stockdiary.application.principlegroup.dto.request.CreatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.ReorderPrincipleGroupsRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.UpdatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.response.DefaultPrincipleGroupResponse;
import com.ogd.stockdiary.application.principlegroup.dto.response.PrincipleGroupResponse;
import com.ogd.stockdiary.application.principlegroup.dto.response.RecommendationsResponse;
import com.ogd.stockdiary.application.principlegroup.dto.response.RecommendedPrincipleGroupResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.principlegroup.dto.CreatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.ReorderPrincipleGroupsCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.UpdatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.usecase.PrincipleGroupUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/principle-groups")
@RequiredArgsConstructor
@Tag(name = "Principle Group", description = "투자원칙 그룹 관리 API")
public class PrincipleGroupController {

    private final PrincipleGroupUseCase principleGroupUseCase;
    private final InvestmentPrincipleRepository investmentPrincipleRepository;
    private final PrincipleGroupMapper principleGroupMapper;

    @GetMapping
    @Operation(summary = "투자원칙 그룹 목록 조회", description = "사용자의 모든 투자원칙 그룹과 속한 원칙들을 조회합니다.")
    public HttpApiResponse<List<PrincipleGroupResponse>> getUserPrincipleGroups(
        @AuthenticationPrincipal AuthenticatedUser user,
        @RequestParam(required = false) PrincipleType type) {

        Long userId = user.getUserId();

        List<PrincipleGroup> principleGroups = principleGroupUseCase.getUserPrincipleGroups(userId, type);

        List<PrincipleGroupResponse> responses = principleGroups.stream()
            .sorted(Comparator.comparing(PrincipleGroup::getId).reversed())
            .map(
                group -> {
                    List<InvestmentPrinciple> principles = investmentPrincipleRepository.findByPrincipleGroupId(
                        group.getId());
                    return principleGroupMapper.toResponse(group, principles);
                })
            .collect(Collectors.toList());

        return HttpApiResponse.of(responses);
    }

    @GetMapping("/systems")
    @Operation(summary = "추천 및 기본 투자원칙 그룹 목록 조회", description = "시스템 추천 투자원칙 그룹과 기본 투자원칙 그룹 목록을 조회합니다.")
    public HttpApiResponse<RecommendationsResponse> getRecommendationPrincipleGroups() {
        // RECOMMEND 타입 그룹 조회
        List<PrincipleGroup> recommendedGroups = principleGroupUseCase.getRecommendedPrincipleGroups();
        List<RecommendedPrincipleGroupResponse> recommendedResponses = recommendedGroups.stream()
            .sorted(Comparator.comparing(PrincipleGroup::getId))
            .map(
                group -> {
                    int principleCount = investmentPrincipleRepository.findByPrincipleGroupId(
                        group.getId()).size();
                    return principleGroupMapper.toRecommendedResponse(group, principleCount);
                })
            .collect(Collectors.toList());

        // DEFAULT 타입 그룹 조회 (간소화된 정보만)
        List<PrincipleGroup> defaultGroups = principleGroupUseCase.getDefaultPrincipleGroups();
        List<DefaultPrincipleGroupResponse> defaultResponses = defaultGroups.stream()
            .sorted(Comparator.comparing(PrincipleGroup::getId))
            .map(principleGroupMapper::toDefaultResponse)
            .collect(Collectors.toList());

        RecommendationsResponse response = new RecommendationsResponse(recommendedResponses, defaultResponses);
        return HttpApiResponse.of(response);
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "투자원칙 그룹 조회", description = "특정 투자원칙 그룹과 속한 원칙들을 조회합니다.")
    public HttpApiResponse<PrincipleGroupResponse> getPrincipleGroupById(
        @AuthenticationPrincipal AuthenticatedUser user,
        @PathVariable Long groupId) {

        Long userId = user.getUserId();

        PrincipleGroup principleGroup = principleGroupUseCase.getPrincipleGroupById(groupId, userId);

        List<InvestmentPrinciple> principles = investmentPrincipleRepository
            .findByPrincipleGroupId(principleGroup.getId());
        PrincipleGroupResponse response = principleGroupMapper.toResponse(principleGroup, principles);

        return HttpApiResponse.of(response);
    }

    @PostMapping
    @Operation(summary = "투자원칙 그룹 생성", description = "새로운 투자원칙 그룹을 생성합니다. principles가 있으면 함께 생성됩니다. (최대 5개)")
    public HttpApiResponse<PrincipleGroupResponse> createPrincipleGroup(
        @AuthenticationPrincipal AuthenticatedUser user,
        @Valid @RequestBody CreatePrincipleGroupRequest request) {

        Long userId = user.getUserId();

        CreatePrincipleGroupCommand command = principleGroupMapper.toCommand(request, userId);
        PrincipleGroup principleGroup = principleGroupUseCase.createPrincipleGroup(command);

        List<InvestmentPrinciple> principles = investmentPrincipleRepository
            .findByPrincipleGroupId(principleGroup.getId());
        PrincipleGroupResponse response = principleGroupMapper.toResponse(principleGroup, principles);

        return HttpApiResponse.of(response);
    }

    @PatchMapping("/{groupId}")
    @Operation(summary = "투자원칙 그룹 수정", description = "투자원칙 그룹의 이름을 수정합니다.")
    public HttpApiResponse<PrincipleGroupResponse> updatePrincipleGroup(
        @AuthenticationPrincipal AuthenticatedUser user,
        @PathVariable Long groupId,
        @Valid @RequestBody UpdatePrincipleGroupRequest request) {

        Long userId = user.getUserId();

        UpdatePrincipleGroupCommand command = principleGroupMapper.toCommand(request, groupId, userId);
        PrincipleGroup principleGroup = principleGroupUseCase.updatePrincipleGroup(command);

        List<InvestmentPrinciple> principles = investmentPrincipleRepository
            .findByPrincipleGroupId(principleGroup.getId());
        PrincipleGroupResponse response = principleGroupMapper.toResponse(principleGroup, principles);

        return HttpApiResponse.of(response);
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "투자원칙 그룹 삭제", description = "투자원칙 그룹과 해당 그룹에 속한 모든 원칙들을 삭제합니다.")
    public HttpApiResponse<Void> deletePrincipleGroup(
        @AuthenticationPrincipal AuthenticatedUser user,
        @PathVariable Long groupId) {

        Long userId = user.getUserId();

        principleGroupUseCase.deletePrincipleGroup(groupId, userId);

        return HttpApiResponse.of("success");
    }

    @PatchMapping("/reorder")
    @Operation(summary = "투자원칙 그룹 순서 변경", description = "여러 투자원칙 그룹의 표시 순서를 한번에 변경합니다.")
    public HttpApiResponse<Void> reorderPrincipleGroups(
        @AuthenticationPrincipal AuthenticatedUser user,
        @Valid @RequestBody ReorderPrincipleGroupsRequest request) {

        Long userId = user.getUserId();

        ReorderPrincipleGroupsCommand command = principleGroupMapper.toReorderCommand(request, userId);
        principleGroupUseCase.reorderPrincipleGroups(command);

        return HttpApiResponse.of("success");
    }
}
