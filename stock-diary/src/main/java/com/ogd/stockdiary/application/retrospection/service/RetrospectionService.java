package com.ogd.stockdiary.application.retrospection.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ogd.stockdiary.application.image.repository.JpaImageMetadataRepository;
import com.ogd.stockdiary.application.image.repository.JpaPrincipleCheckImageRepository;
import com.ogd.stockdiary.application.principlecheck.repository.JpaPrincipleCheckLinkRepository;
import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.image.entity.ImageMetadata;
import com.ogd.stockdiary.domain.image.entity.ImageStatus;
import com.ogd.stockdiary.domain.image.entity.PrincipleCheckImage;
import com.ogd.stockdiary.domain.image.port.in.ImageUseCase;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.investmentprinciple.port.out.InvestmentPrincipleRepository;
import com.ogd.stockdiary.domain.principlecheck.dto.PrincipleCheckCommand;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckLink;
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
    private final JpaImageMetadataRepository imageMetadataRepository;
    private final JpaPrincipleCheckImageRepository principleCheckImageRepository;
    private final JpaPrincipleCheckLinkRepository principleCheckLinkRepository;
    private final ImageUseCase imageUseCase;

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
        principleCheckCommands.forEach(command -> {
            // 투자원칙 조회
            InvestmentPrinciple principle = investmentPrincipleRepository
                .findByIdAndUserId(command.getPrincipleId(), userId)
                .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003,
                    "투자원칙을 찾을 수 없습니다: " + command.getPrincipleId()));

            // PrincipleCheck 생성 및 저장
            PrincipleCheck principleCheck = PrincipleCheck.create(
                retrospection, principle, command.getStatus(), command.getReason());
            PrincipleCheck savedPrincipleCheck = principleCheckRepository.save(principleCheck);

            // 이미지 처리
            if (!CollectionUtils.isEmpty(command.getImageIds())) {
                saveImages(savedPrincipleCheck, command.getImageIds(), userId);
            }

            // 링크 처리
            if (!CollectionUtils.isEmpty(command.getLinks())) {
                saveLinks(savedPrincipleCheck, command.getLinks());
            }
        });
    }

    private void saveImages(PrincipleCheck principleCheck, List<Long> imageIds, Long userId) {
        List<ImageMetadata> images = imageMetadataRepository.findAllById(imageIds);

        // 이미지 존재 여부 및 소유권 검증
        if (images.size() != imageIds.size()) {
            throw new ApplicationException(CodeEnum.FRS_003, "일부 이미지를 찾을 수 없습니다");
        }

        images.forEach(image -> {
            if (!image.getUser().getId().equals(userId)) {
                throw new ApplicationException(CodeEnum.FRS_003,
                    "해당 이미지에 대한 권한이 없습니다: " + image.getId());
            }
        });

        // PrincipleCheckImage 매핑 생성
        List<PrincipleCheckImage> principleCheckImages = images.stream()
            .map(image -> PrincipleCheckImage.create(principleCheck, image))
            .toList();
        principleCheckImageRepository.saveAll(principleCheckImages);

        // 이미지 상태를 C(저장완료)로 변경
        images.forEach(image -> image.updateStatus(ImageStatus.C));
        imageMetadataRepository.saveAll(images);
    }

    private void saveLinks(PrincipleCheck principleCheck, List<String> links) {
        List<PrincipleCheckLink> principleCheckLinks = links.stream()
            .map(link -> PrincipleCheckLink.create(principleCheck, link))
            .toList();
        principleCheckLinkRepository.saveAll(principleCheckLinks);
    }

    @Override
    @Transactional(readOnly = true)
    public GetRetrospectionResponse getRetrospection(Long retrospectionId, Long userId) {
        Retrospection retrospection = retrospectionRepository.findByIdAndUserId(retrospectionId, userId);
        List<PrincipleCheck> principleChecks = principleCheckRepository.findByRetrospectionId(retrospectionId);

        // PrincipleCheck ID별로 이미지 URL 목록 생성
        Map<Long, List<String>> imageUrlsMap = principleChecks.stream()
            .collect(Collectors.toMap(
                PrincipleCheck::getId,
                pc -> getImageUrls(pc.getId())));

        // PrincipleCheck ID별로 링크 목록 생성
        Map<Long, List<String>> linksMap = principleChecks.stream()
            .collect(Collectors.toMap(
                PrincipleCheck::getId,
                pc -> getLinks(pc.getId())));

        return RetrospectionMapper.toGetResponse(retrospection, principleChecks, imageUrlsMap, linksMap);
    }

    private List<String> getImageUrls(Long principleCheckId) {
        List<PrincipleCheckImage> principleCheckImages = principleCheckImageRepository
            .findByPrincipleCheckId(principleCheckId);

        return principleCheckImages.stream()
            .map(pci -> imageUseCase.getDownloadUrl(pci.getImage().getObjectKey()))
            .toList();
    }

    private List<String> getLinks(Long principleCheckId) {
        List<PrincipleCheckLink> principleCheckLinks = principleCheckLinkRepository
            .findByPrincipleCheckId(principleCheckId);

        return principleCheckLinks.stream()
            .map(PrincipleCheckLink::getLinkUrl)
            .toList();
    }
}
