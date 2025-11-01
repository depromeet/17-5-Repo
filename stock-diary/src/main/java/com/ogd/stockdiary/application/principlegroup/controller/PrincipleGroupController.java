package com.ogd.stockdiary.application.principlegroup.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.ogd.stockdiary.application.principlegroup.dto.mapper.PrincipleGroupMapper;
import com.ogd.stockdiary.application.principlegroup.dto.request.CreatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.ReorderPrincipleGroupsRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.UpdatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.response.PrincipleGroupResponse;
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
        @RequestParam(required = false) PrincipleType type) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

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

    @GetMapping("/{groupId}")
    @Operation(summary = "투자원칙 그룹 조회", description = "특정 투자원칙 그룹과 속한 원칙들을 조회합니다.")
    public HttpApiResponse<PrincipleGroupResponse> getPrincipleGroupById(
        @PathVariable Long groupId) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        PrincipleGroup principleGroup = principleGroupUseCase.getPrincipleGroupById(groupId, userId);

        List<InvestmentPrinciple> principles = investmentPrincipleRepository
            .findByPrincipleGroupId(principleGroup.getId());
        PrincipleGroupResponse response = principleGroupMapper.toResponse(principleGroup, principles);

        return HttpApiResponse.of(response);
    }

    @PostMapping
    @Operation(summary = "투자원칙 그룹 생성", description = "새로운 투자원칙 그룹을 생성합니다. principles가 있으면 함께 생성됩니다. (최대 5개)")
    public HttpApiResponse<PrincipleGroupResponse> createPrincipleGroup(
        @Valid @RequestBody CreatePrincipleGroupRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

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
        @PathVariable Long groupId, @Valid @RequestBody UpdatePrincipleGroupRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        UpdatePrincipleGroupCommand command = principleGroupMapper.toCommand(request, groupId, userId);
        PrincipleGroup principleGroup = principleGroupUseCase.updatePrincipleGroup(command);

        List<InvestmentPrinciple> principles = investmentPrincipleRepository
            .findByPrincipleGroupId(principleGroup.getId());
        PrincipleGroupResponse response = principleGroupMapper.toResponse(principleGroup, principles);

        return HttpApiResponse.of(response);
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "투자원칙 그룹 삭제", description = "투자원칙 그룹과 해당 그룹에 속한 모든 원칙들을 삭제합니다.")
    public HttpApiResponse<Void> deletePrincipleGroup(@PathVariable Long groupId) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        principleGroupUseCase.deletePrincipleGroup(groupId, userId);

        return HttpApiResponse.of("success");
    }

    @PatchMapping("/reorder")
    @Operation(summary = "투자원칙 그룹 순서 변경", description = "여러 투자원칙 그룹의 표시 순서를 한번에 변경합니다.")
    public HttpApiResponse<Void> reorderPrincipleGroups(
        @Valid @RequestBody ReorderPrincipleGroupsRequest request) {

        // TODO: Spring Security에서 User 정보 가져오기
        Long userId = 1L; // 임시로 하드코딩

        ReorderPrincipleGroupsCommand command = principleGroupMapper.toReorderCommand(request, userId);
        principleGroupUseCase.reorderPrincipleGroups(command);

        return HttpApiResponse.of("success");
    }
}
