package com.ogd.stockdiary.application.report.repository;

import static com.ogd.stockdiary.domain.image.entity.QImageMetadata.imageMetadata;
import static com.ogd.stockdiary.domain.image.entity.QPrincipleCheckImage.principleCheckImage;
import static com.ogd.stockdiary.domain.investmentprinciple.entity.QInvestmentPrinciple.investmentPrinciple;
import static com.ogd.stockdiary.domain.principlecheck.entity.QPrincipleCheck.principleCheck;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.image.port.in.ImageUseCase;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;
import com.ogd.stockdiary.domain.report.port.out.ReportDataPort;
import com.ogd.stockdiary.domain.report.vo.ReportSourceData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PrincipleCheckQueryDslAdapter implements ReportDataPort {

    private final JPAQueryFactory jpaQueryFactory;
    private final ImageUseCase imageUseCase;

    @Override
    public List<ReportSourceData> findByRetrospectionId(Long retrospectionId) {
        // 1차 쿼리: 원칙 체크 정보 조회
        List<ReportSourceProjectionDto> projectionDtos = jpaQueryFactory
            .select(Projections.constructor(ReportSourceProjectionDto.class,
                principleCheck.id,
                investmentPrinciple.principle,
                principleCheck.status,
                principleCheck.reason))
            .from(principleCheck)
            .join(principleCheck.principle, investmentPrinciple)
            .where(principleCheck.retrospection.id.eq(retrospectionId))
            .fetch();

        // 2차 쿼리: 이미지 URL 맵 조회
        List<Long> principleCheckIds = projectionDtos.stream()
            .map(ReportSourceProjectionDto::principleCheckId)
            .toList();

        Map<Long, List<String>> presignedUrlMap = getImageUrlsMap(principleCheckIds);

        // ReportSourceData로 변환
        return projectionDtos.stream()
            .map(dto -> new ReportSourceData(
                dto.principleCheckId(),
                dto.principle(),
                dto.status(),
                dto.reason(),
                presignedUrlMap.getOrDefault(dto.principleCheckId(), List.of())))
            .toList();
    }

    private Map<Long, List<String>> getImageUrlsMap(List<Long> checkIds) {
        if (checkIds.isEmpty()) {
            return Map.of();
        }

        // QueryDSL로 이미지 메타데이터 조회
        List<ImageKeyDto> imageKeyDtos = jpaQueryFactory
            .select(Projections.constructor(ImageKeyDto.class,
                principleCheckImage.principleCheck.id,
                imageMetadata.objectKey))
            .from(principleCheckImage)
            .join(principleCheckImage.image, imageMetadata)
            .where(principleCheckImage.principleCheck.id.in(checkIds))
            .fetch();

        // objectKey를 URL로 변환하고 그룹화
        return imageKeyDtos.stream()
            .collect(Collectors.groupingBy(
                ImageKeyDto::checkId,
                Collectors.mapping(
                    dto -> imageUseCase.getDownloadUrl(dto.objectKey()),
                    Collectors.toList())));
    }

    // 중간 dto
    public static record ReportSourceProjectionDto(
        Long principleCheckId,
        String principle,
        PrincipleCheckStatus status,
        String reason) {

        public ReportSourceProjectionDto {
        }
    }

    // 중간 dto
    public static record ImageKeyDto(
        Long checkId,
        String objectKey) {

        public ImageKeyDto {
        }
    }
}
