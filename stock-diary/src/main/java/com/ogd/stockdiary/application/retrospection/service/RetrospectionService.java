package com.ogd.stockdiary.application.retrospection.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.principlecheck.dto.PrincipleCheckCommand;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;
import com.ogd.stockdiary.domain.principlecheck.port.out.PrincipleCheckRepository;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetrospectionService implements CreateRetrospectionUseCase, GetRetrospectionUseCase {

    private final RetrospectionRepository retrospectionRepository;
    private final UserRepository userRepository;
    private final PrincipleCheckRepository principleCheckRepository;
    private final InvestmentPrincipleRepository investmentPrincipleRepository;

    @Override
    @Transactional
    public Retrospection createRetrospection(CreateRetrospectionCommand command) {
        // 사용자 조회
        User user = userRepository
            .findById(command.getUserId())
            .orElseThrow(
                () -> new ApplicationException(
                    CodeEnum.FRS_003,
                    "사용자를 찾을 수 없습니다: " + command.getUserId()));

        // 엔티티 생성
        Retrospection retrospection = RetrospectionMapper.toEntity(command, user);

        // 회고 저장
        Retrospection savedRetrospection = retrospectionRepository.save(retrospection);

        // 원칙 체크 저장
        if (!CollectionUtils.isEmpty(command.getPrincipleChecks())) {
            savePrincipleChecks(savedRetrospection, command.getPrincipleChecks(), user.getId());
        }

        return savedRetrospection;
    }

    private void savePrincipleChecks(
        Retrospection retrospection,
        List<PrincipleCheckCommand> principleCheckCommands,
        Long userId) {
        List<PrincipleCheck> principleChecks = principleCheckCommands.stream()
            .map(command -> {
                InvestmentPrinciple principle = investmentPrincipleRepository
                    .findByIdAndUserId(command.getPrincipleId(), userId)
                    .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003,
                        "투자원칙을 찾을 수 없습니다: " + command.getPrincipleId()));

                return PrincipleCheck.create(retrospection, principle, command.getStatus());
            })
            .toList();

        principleCheckRepository.saveAll(principleChecks);
    }

    @Override
    @Transactional(readOnly = true)
    public GetRetrospectionResponse getRetrospection(Long retrospectionId, Long userId) {
        Retrospection retrospection = retrospectionRepository.findByIdAndUserId(retrospectionId, userId);
        List<PrincipleCheck> principleChecks = principleCheckRepository.findByRetrospectionId(retrospectionId);

        return RetrospectionMapper.toGetResponse(retrospection, principleChecks);
    }
}
