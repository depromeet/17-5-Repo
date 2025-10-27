package com.ogd.stockdiary.domain.principlegroup.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.principlegroup.dto.CreatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.ReorderPrincipleGroupsCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.UpdatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.port.out.PrincipleGroupRepository;
import com.ogd.stockdiary.domain.principlegroup.usecase.PrincipleGroupUseCase;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PrincipleGroupService implements PrincipleGroupUseCase {

    private final PrincipleGroupRepository principleGroupRepository;
    private final InvestmentPrincipleRepository investmentPrincipleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PrincipleGroup> getUserPrincipleGroups(Long userId) {
        return principleGroupRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PrincipleGroup getPrincipleGroupById(Long groupId, Long userId) {
        return principleGroupRepository
            .findByIdAndUserId(groupId, userId)
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003, "투자원칙 그룹을 찾을 수 없습니다: " + groupId));
    }

    @Override
    public PrincipleGroup createPrincipleGroup(CreatePrincipleGroupCommand command) {
        User user = userRepository
            .findById(command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "사용자를 찾을 수 없습니다: " + command.getUserId()));

        // 그룹 생성 시 원칙 개수 검증
        if (command.getPrinciples() != null
            && command.getPrinciples().size() > InvestmentPrinciple.MAX_PRINCIPLES_PER_GROUP) {
            throw new ApplicationException(
                CodeEnum.FRS_005,
                "그룹당 최대 " + InvestmentPrinciple.MAX_PRINCIPLES_PER_GROUP + "개의 투자원칙만 추가할 수 있습니다");
        }

        // 그룹 생성
        PrincipleGroup principleGroup = PrincipleGroup.create(user, command.getGroupName(), command.getDisplayOrder());
        PrincipleGroup savedGroup = principleGroupRepository.save(principleGroup);

        // 원칙들이 있으면 함께 생성
        if (command.getPrinciples() != null && !command.getPrinciples().isEmpty()) {
            List<InvestmentPrinciple> principles = IntStream.range(0, command.getPrinciples().size())
                .mapToObj(
                    index -> InvestmentPrinciple.create(
                        user,
                        savedGroup,
                        command.getPrincipleType(),
                        command.getPrinciples().get(index),
                        index))
                .collect(Collectors.toList());

            investmentPrincipleRepository.saveAll(principles);
        }

        return savedGroup;
    }

    @Override
    public PrincipleGroup updatePrincipleGroup(UpdatePrincipleGroupCommand command) {
        PrincipleGroup principleGroup = principleGroupRepository
            .findByIdAndUserId(command.getGroupId(), command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "투자원칙 그룹을 찾을 수 없습니다: " + command.getGroupId()));

        principleGroup.updateGroupName(command.getGroupName());
        return principleGroup;
    }

    @Override
    public void deletePrincipleGroup(Long groupId, Long userId) {
        if (!principleGroupRepository.findByIdAndUserId(groupId, userId).isPresent()) {
            throw new ApplicationException(CodeEnum.FRS_003, "투자원칙 그룹을 찾을 수 없습니다: " + groupId);
        }

        // 그룹에 속한 원칙들 먼저 삭제
        investmentPrincipleRepository.deleteByPrincipleGroupId(groupId);

        // 그룹 삭제
        principleGroupRepository.deleteByIdAndUserId(groupId, userId);
    }

    @Override
    public void reorderPrincipleGroups(ReorderPrincipleGroupsCommand command) {
        for (ReorderPrincipleGroupsCommand.GroupOrder groupOrder : command.getGroupOrders()) {
            PrincipleGroup principleGroup = principleGroupRepository
                .findByIdAndUserId(groupOrder.getGroupId(), command.getUserId())
                .orElseThrow(
                    () -> new ApplicationException(
                        CodeEnum.FRS_003,
                        "투자원칙 그룹을 찾을 수 없습니다: "
                            + groupOrder.getGroupId()));

            principleGroup.updateDisplayOrder(groupOrder.getDisplayOrder());
        }
    }
}
