package com.ogd.stockdiary.application.report.repository;

import static com.ogd.stockdiary.domain.image.entity.QPrincipleCheckImage.principleCheckImage;
import static com.ogd.stockdiary.domain.investmentprinciple.entity.QInvestmentPrinciple.investmentPrinciple;
import static com.ogd.stockdiary.domain.principlecheck.entity.QPrincipleCheck.principleCheck;
import static com.ogd.stockdiary.domain.principlecheck.entity.QPrincipleCheckLink.principleCheckLink;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.domain.report.port.out.PrincipleCheckPort;
import com.ogd.stockdiary.domain.report.vo.PrincipleCheckData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PrincipleCheckQueryDslAdapter implements PrincipleCheckPort {

    private final JPAQueryFactory queryFactory;
    private final FileClientPort fileClientPort;

    @Override
    public List<PrincipleCheckData> findByRetrospectionId(Long retrospectionId) {
        // 1. PrincipleCheck 기본 데이터 조회
        List<PrincipleCheckDto> checks = queryFactory
            .select(Projections.constructor(PrincipleCheckDto.class,
                principleCheck.id,
                investmentPrinciple.principle,
                principleCheck.status.stringValue(),
                principleCheck.reason))
            .from(principleCheck)
            .join(principleCheck.principle, investmentPrinciple)
            .where(principleCheck.retrospection.id.eq(retrospectionId))
            .fetch();

        if (checks.isEmpty()) {
            return List.of();
        }

        // 2. checkId 리스트 추출
        List<Long> checkIds = checks.stream()
            .map(PrincipleCheckDto::checkId)
            .collect(Collectors.toList());

        // 3. 이미지 다운로드 URL 맵 조회 (checkId -> List<downloadUrl>)
        Map<Long, List<String>> imageUrlsMap = getImageUrlsMap(checkIds);

        // 4. 링크 맵 조회 (checkId -> List<linkUrl>)
        Map<Long, List<String>> linksMap = getLinksMap(checkIds);

        // 5. VO로 변환
        return checks.stream()
            .map(dto -> new PrincipleCheckData(
                dto.principleName,
                dto.status,
                dto.reason,
                imageUrlsMap.getOrDefault(dto.checkId, List.of()),
                linksMap.getOrDefault(dto.checkId, List.of())))
            .collect(Collectors.toList());
    }

    /**
     * 이미지 다운로드 URL 맵 조회
     * objectKey를 조회한 후 download pre-signed URL로 변환
     */
    private Map<Long, List<String>> getImageUrlsMap(List<Long> checkIds) {
        List<ImageKeyDto> imageKeyDtos = queryFactory
            .select(Projections.constructor(ImageKeyDto.class,
                principleCheckImage.principleCheck.id,
                principleCheckImage.image.objectKey))
            .from(principleCheckImage)
            .where(principleCheckImage.principleCheck.id.in(checkIds))
            .fetch();

        // objectKey를 download URL로 변환
        return imageKeyDtos.stream()
            .collect(Collectors.groupingBy(
                ImageKeyDto::checkId,
                Collectors.mapping(
                    dto -> fileClientPort.getDownloadPreSignedUrl(dto.objectKey, 3600),
                    Collectors.toList())));
    }

    /**
     * 링크 맵 조회
     */
    private Map<Long, List<String>> getLinksMap(List<Long> checkIds) {
        List<LinkDto> linkDtos = queryFactory
            .select(Projections.constructor(LinkDto.class,
                principleCheckLink.principleCheck.id,
                principleCheckLink.linkUrl))
            .from(principleCheckLink)
            .where(principleCheckLink.principleCheck.id.in(checkIds))
            .fetch();

        return linkDtos.stream()
            .collect(Collectors.groupingBy(
                LinkDto::checkId,
                Collectors.mapping(LinkDto::linkUrl, Collectors.toList())));
    }

    /**
     * 중간 DTO - PrincipleCheck 데이터
     */
    private record PrincipleCheckDto(
        Long checkId,
        String principleName,
        String status,
        String reason) {
    }

    /**
     * 중간 DTO - 이미지 objectKey (download URL 변환용)
     */
    private record ImageKeyDto(
        Long checkId,
        String objectKey) {
    }

    /**
     * 중간 DTO - 링크
     */
    private record LinkDto(
        Long checkId,
        String linkUrl) {
    }
}
