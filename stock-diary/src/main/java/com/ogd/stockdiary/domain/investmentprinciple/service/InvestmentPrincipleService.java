package com.ogd.stockdiary.domain.investmentprinciple.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.investmentprinciple.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessResult;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.investmentprinciple.usecase.InvestmentPrincipleUseCase;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class InvestmentPrincipleService implements InvestmentPrincipleUseCase {

  private final InvestmentPrincipleRepository investmentPrincipleRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public List<InvestmentPrinciple> getUserPrinciples(Long userId) {
    return investmentPrincipleRepository.findByUserId(userId);
  }

  @Override
  public InvestmentPrinciple createPrinciple(CreatePrincipleCommand command) {
    User user = userRepository.findById(command.getUserId()).orElseThrow(
        () -> new ApplicationException(CodeEnum.FRS_003, "사용자를 찾을 수 없습니다: " + command.getUserId()));

    InvestmentPrinciple principle = InvestmentPrinciple.create(user, command.getPrinciple());
    return investmentPrincipleRepository.save(principle);
  }

  @Override
  public List<InvestmentPrinciple> createMultiplePrinciples(CreateMultiplePrinciplesCommand command) {
    User user = userRepository.findById(command.getUserId()).orElseThrow(
        () -> new ApplicationException(CodeEnum.FRS_003, "사용자를 찾을 수 없습니다: " + command.getUserId()));

    List<InvestmentPrinciple> principles = command.getPrinciples().stream()
        .map(principleText -> InvestmentPrinciple.create(user, principleText)).collect(Collectors.toList());

    return investmentPrincipleRepository.saveAll(principles);
  }

  @Override
  public InvestmentPrinciple updatePrinciple(UpdatePrincipleCommand command) {
    InvestmentPrinciple principle = investmentPrincipleRepository
        .findByIdAndUserId(command.getPrincipleId(), command.getUserId())
        .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003,
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
    if (command.getCreatePrinciples() != null && !command.getCreatePrinciples().isEmpty()) {
      User user = userRepository.findById(command.getUserId()).orElseThrow(
          () -> new ApplicationException(CodeEnum.FRS_003, "사용자를 찾을 수 없습니다: " + command.getUserId()));

      List<InvestmentPrinciple> principles = command.getCreatePrinciples().stream()
          .map(principleText -> InvestmentPrinciple.create(user, principleText)).collect(Collectors.toList());
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
      investmentPrincipleRepository.deleteAllByIdInAndUserId(command.getDeletePrincipleIds(),
          command.getUserId());
      deletedPrincipleIds = command.getDeletePrincipleIds();
    }

    return new BatchProcessResult(createdPrinciples, updatedPrinciples, deletedPrincipleIds);
  }
}
