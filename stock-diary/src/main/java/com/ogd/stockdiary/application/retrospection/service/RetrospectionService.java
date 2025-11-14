package com.ogd.stockdiary.application.retrospection.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroupType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ogd.stockdiary.application.image.repository.JpaImageMetadataRepository;
import com.ogd.stockdiary.application.image.repository.JpaPrincipleCheckImageRepository;
import com.ogd.stockdiary.application.principlecheck.repository.JpaPrincipleCheckLinkRepository;
import com.ogd.stockdiary.application.retrospection.dto.mapper.RetrospectionMapper;
import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.MarketGroupResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.RetrospectionDetailResponse;
import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
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
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import com.ogd.stockdiary.domain.retrospection.entity.Memo;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.*;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionUseCase;
import com.ogd.stockdiary.domain.retrospection.port.out.MemoRepository;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;
import com.ogd.stockdiary.domain.stock.entity.Market;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetrospectionService
    implements
        CreateRetrospectionUseCase,
        GetRetrospectionUseCase,
        DeleteRetrospectionUseCase,
        CreateMemoUseCase,
        UpdateMemoUseCase,
        DeleteMemoUseCase {

    private final RetrospectionRepository retrospectionRepository;
    private final UserRepository userRepository;
    private final PrincipleCheckRepository principleCheckRepository;
    private final InvestmentPrincipleRepository investmentPrincipleRepository;
    private final JpaImageMetadataRepository imageMetadataRepository;
    private final JpaPrincipleCheckImageRepository principleCheckImageRepository;
    private final JpaPrincipleCheckLinkRepository principleCheckLinkRepository;
    private final ImageUseCase imageUseCase;
    private final MemoRepository memoRepository;
    private final StockRepository stockRepository;
    private final FileClientPort fileClientPort;
    private final FeedbackRepository feedbackRepository;

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
                .findById(command.getPrincipleId())
                .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003,
                    "투자원칙을 찾을 수 없습니다: " + command.getPrincipleId()));

            // USER 타입 원칙인 경우 userId 검증
            if (principle.getPrincipleGroup().getGroupType() == PrincipleGroupType.USER) {
                if (!principle.getUser().getId().equals(userId)) {
                    throw new ApplicationException(CodeEnum.FRS_003,
                        "해당 투자원칙에 접근할 권한이 없습니다: " + command.getPrincipleId());
                }
            }

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

        // 메모 목록 조회 (최신순 정렬)
        List<Memo> memos = memoRepository.findByRetrospectionIdOrderByIdDesc(retrospectionId);

        // Stock 조회 (symbol과 market으로)
        Stock stock = null;
        String companyLogoUrl = null;
        try {
            Market market = Market.valueOf(retrospection.getMarket());
            Optional<Stock> stockOptional = stockRepository.findByCodeAndMarket(retrospection.getSymbol(), market);
            stock = stockOptional.orElse(null);

            // Stock의 logo가 있으면 URL 생성
            if (stock != null && stock.getLogo() != null) {
                companyLogoUrl = imageUseCase.getDownloadUrl(stock.getLogo());
            }
        } catch (IllegalArgumentException e) {
            // Market enum 변환 실패 시 stock은 null로 유지
        }

        // Feedback 조회
        Optional<Feedback> feedbackOptional = feedbackRepository.findByRetrospectionId(retrospectionId);
        Feedback feedback = feedbackOptional.orElse(null);

        return RetrospectionMapper.toGetResponse(retrospection, principleChecks, imageUrlsMap, linksMap, memos, stock,
            companyLogoUrl, feedback);
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

    @Override
    public List<MarketGroupResponse> getAllRetrospections(GetRetrospectionCommand command) {

        // 사용자의 모든 회고 조회
        List<Retrospection> retrospections = retrospectionRepository.findAllByUserId(command.userId());

        // 회고에서 symbol 목록 추출해서 stock 정보 조회
        List<String> symbol = retrospections.stream().map(Retrospection::getSymbol).distinct().toList();
        List<Stock> stocks = stockRepository.findAllByCodeIn(symbol);

        // stock 정보를 Map 으로 변환
        Map<String, Stock> stockByCompanyName = stocks.stream()
            .collect(Collectors.toMap(
                Stock::getCode, // 키: stock의 code
                stock -> stock));

        // companyName 기준 회고 그룹화
        Map<Stock, List<Retrospection>> retrospectionsByCompanyName = retrospections.stream()
            .collect(Collectors.groupingBy(retrospection
            // 키: retrospection 의 symbol 에 해당하는 Stock 객체
            -> stockByCompanyName.getOrDefault(retrospection.getSymbol(), new Stock())));

        // 응답 DTO 로 변환
        return retrospectionsByCompanyName.entrySet().stream()
            .map(entry -> {
                Stock stock = entry.getKey();
                List<RetrospectionDetailResponse> detailResponses = entry.getValue().stream()
                    .map(RetrospectionDetailResponse::fromEntity)
                    .sorted(Comparator.comparingLong(RetrospectionDetailResponse::id).reversed())
                    .toList();

                String logo = null;
                if (stock.getLogo() != null && !stock.getLogo().isEmpty()) {
                    logo = fileClientPort.getDownloadPreSignedUrl(stock.getLogo(), 86400); // 24시간
                }

                return new MarketGroupResponse(
                    stock.getCompanyName(),
                    logo,
                    stock.getCode(),
                    stock.getMarket(),
                    detailResponses);

            })
            .sorted(
                Comparator.comparingLong((MarketGroupResponse group) -> group.retrospections().get(0).id()).reversed())
            .toList();

    }

    // Memo CRUD operations

    @Override
    @Transactional
    public Memo createMemo(Long retrospectionId, String content, Long userId) {
        // 회고 존재 및 권한 확인
        Retrospection retrospection = retrospectionRepository.findByIdAndUserId(retrospectionId, userId);

        // 메모 생성 및 저장
        Memo memo = Memo.create(retrospection, content, userId);
        return memoRepository.save(memo);
    }

    @Override
    @Transactional
    public Memo updateMemo(Long retrospectionId, Long memoId, String content, Long userId) {
        // 회고 권한 확인
        retrospectionRepository.findByIdAndUserId(retrospectionId, userId);

        // 메모 조회
        Memo memo = memoRepository.getById(memoId);

        // 메모 작성자 확인
        if (!memo.getUserId().equals(userId)) {
            throw new ApplicationException(
                CodeEnum.FRS_003,
                "해당 메모에 대한 권한이 없습니다: " + memoId);
        }

        // 메모가 해당 회고에 속하는지 확인
        if (!memo.getRetrospection().getId().equals(retrospectionId)) {
            throw new ApplicationException(
                CodeEnum.FRS_003,
                "해당 메모는 이 회고에 속하지 않습니다: " + memoId);
        }

        // 메모 수정
        memo.updateContent(content);

        return memo;
    }

    @Override
    @Transactional
    public void deleteMemo(Long retrospectionId, Long memoId, Long userId) {
        // 회고 권한 확인
        retrospectionRepository.findByIdAndUserId(retrospectionId, userId);

        // 메모 조회
        Memo memo = memoRepository.getById(memoId);

        // 메모 작성자 확인
        if (!memo.getUserId().equals(userId)) {
            throw new ApplicationException(
                CodeEnum.FRS_003,
                "해당 메모에 대한 권한이 없습니다: " + memoId);
        }

        // 메모가 해당 회고에 속하는지 확인
        if (!memo.getRetrospection().getId().equals(retrospectionId)) {
            throw new ApplicationException(
                CodeEnum.FRS_003,
                "해당 메모는 이 회고에 속하지 않습니다: " + memoId);
        }

        // 메모 삭제
        memoRepository.delete(memo);
    }

    @Override
    @Transactional
    public void deleteRetrospection(Long retrospectionId) {
        retrospectionRepository.deleteById(retrospectionId);
    }
}
