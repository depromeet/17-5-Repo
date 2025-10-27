package com.ogd.stockdiary.domain.investmentprinciple.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessResult;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.ReorderPrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.entity.PrincipleType;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.investmentprinciple.usecase.InvestmentPrincipleUseCase;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.port.out.PrincipleGroupRepository;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InvestmentPrincipleService implements InvestmentPrincipleUseCase {

    private final InvestmentPrincipleRepository investmentPrincipleRepository;
    private final PrincipleGroupRepository principleGroupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentPrinciple> getUserPrinciples(Long userId, PrincipleType type) {
        if (type != null) {
            return investmentPrincipleRepository.findByUserIdAndPrincipleType(userId, type);
        }
        return investmentPrincipleRepository.findByUserId(userId);
    }

    @Override
    public InvestmentPrinciple createPrinciple(CreatePrincipleCommand command) {
        User user = userRepository
            .findById(command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "사용자를 찾을 수 없습니다: " + command.getUserId()));

        InvestmentPrinciple principle;
        if (command.getGroupId() != null) {
            // 그룹이 지정된 경우
            PrincipleGroup principleGroup = principleGroupRepository
                .findByIdAndUserId(command.getGroupId(), command.getUserId())
                .orElseThrow(
                    () -> new ApplicationException(
                        CodeEnum.FRS_003,
                        "투자원칙 그룹을 찾을 수 없습니다: " + command.getGroupId()));

            // 그룹에 속한 원칙 개수 확인
            List<InvestmentPrinciple> existingPrinciples = investmentPrincipleRepository
                .findByPrincipleGroupId(command.getGroupId());
            if (existingPrinciples.size() >= InvestmentPrinciple.MAX_PRINCIPLES_PER_GROUP) {
                throw new ApplicationException(
                    CodeEnum.FRS_005,
                    "그룹당 최대 " + InvestmentPrinciple.MAX_PRINCIPLES_PER_GROUP + "개의 투자원칙만 추가할 수 있습니다");
            }

            principle = InvestmentPrinciple.create(
                user,
                principleGroup,
                command.getPrincipleType(),
                command.getPrinciple(),
                command.getDisplayOrder());
        } else {
            // 그룹이 없는 경우
            principle = InvestmentPrinciple.create(user, command.getPrincipleType(), command.getPrinciple());
        }

        return investmentPrincipleRepository.save(principle);
    }

    @Override
    public List<InvestmentPrinciple> createMultiplePrinciples(
        CreateMultiplePrinciplesCommand command) {
        User user = userRepository
            .findById(command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "사용자를 찾을 수 없습니다: " + command.getUserId()));

        List<InvestmentPrinciple> principles = command.getPrinciples().stream()
            .map(principleText -> InvestmentPrinciple.create(user, command.getPrincipleType(), principleText))
            .collect(Collectors.toList());

        return investmentPrincipleRepository.saveAll(principles);
    }

    @Override
    public InvestmentPrinciple updatePrinciple(UpdatePrincipleCommand command) {
        InvestmentPrinciple principle = investmentPrincipleRepository
            .findByIdAndUserId(command.getPrincipleId(), command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "투자원칙을 찾을 수 없습니다: " + command.getPrincipleId()));

        principle.updatePrinciple(command.getPrinciple());
        return principle;
    }

    @Override
    public void deletePrinciple(Long principleId, Long userId) {
        if (!investmentPrincipleRepository.findByIdAndUserId(principleId, userId).isPresent()) {
            throw new ApplicationException(CodeEnum.FRS_003, "투자원칙을 찾을 수 없습니다: " + principleId);
        }
        investmentPrincipleRepository.deleteByIdAndUserId(principleId, userId);
    }

    @Override
    public BatchProcessResult batchProcessPrinciples(BatchProcessCommand command) {
        List<InvestmentPrinciple> createdPrinciples = new ArrayList<>();
        List<InvestmentPrinciple> updatedPrinciples = new ArrayList<>();
        List<Long> deletedPrincipleIds = new ArrayList<>();

        // 생성
        // Note: 현재 BatchProcess는 그룹 없이 원칙만 생성하므로 그룹당 5개 제한 검증 불필요
        // 추후 그룹 정보가 추가되면 검증 로직 추가 필요
        if (command.getCreatePrinciples() != null && !command.getCreatePrinciples().isEmpty()) {
            User user = userRepository
                .findById(command.getUserId())
                .orElseThrow(
                    () -> new ApplicationException(
                        CodeEnum.FRS_003,
                        "사용자를 찾을 수 없습니다: " + command.getUserId()));

            List<InvestmentPrinciple> principles = command.getCreatePrinciples().stream()
                .map(principleText -> InvestmentPrinciple.create(user, command.getPrincipleType(), principleText))
                .collect(Collectors.toList());
            createdPrinciples = investmentPrincipleRepository.saveAll(principles);
        }

        // 수정
        if (command.getUpdatePrinciples() != null && !command.getUpdatePrinciples().isEmpty()) {
            for (UpdatePrincipleCommand updateCommand : command.getUpdatePrinciples()) {
                InvestmentPrinciple updated = updatePrinciple(updateCommand);
                updatedPrinciples.add(updated);
            }
        }

        // 삭제
        if (command.getDeletePrincipleIds() != null && !command.getDeletePrincipleIds().isEmpty()) {
            investmentPrincipleRepository.deleteAllByIdInAndUserId(
                command.getDeletePrincipleIds(), command.getUserId());
            deletedPrincipleIds = command.getDeletePrincipleIds();
        }

        return new BatchProcessResult(createdPrinciples, updatedPrinciples, deletedPrincipleIds);
    }

    @Override
    public void reorderPrinciples(ReorderPrinciplesCommand command) {
        for (ReorderPrinciplesCommand.PrincipleOrder principleOrder : command.getPrincipleOrders()) {
            InvestmentPrinciple principle = investmentPrincipleRepository
                .findByIdAndUserId(principleOrder.getPrincipleId(), command.getUserId())
                .orElseThrow(
                    () -> new ApplicationException(
                        CodeEnum.FRS_003,
                        "투자원칙을 찾을 수 없습니다: "
                            + principleOrder.getPrincipleId()));

            principle.updateDisplayOrder(principleOrder.getDisplayOrder());
        }
    }
}
