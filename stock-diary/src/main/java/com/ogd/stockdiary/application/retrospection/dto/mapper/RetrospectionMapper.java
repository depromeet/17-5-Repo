package com.ogd.stockdiary.application.retrospection.dto.mapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ogd.stockdiary.application.principlecheck.dto.request.PrincipleCheckRequest;
import com.ogd.stockdiary.application.retrospection.dto.request.CreateRetrospectionRequest;
import com.ogd.stockdiary.application.retrospection.dto.response.CreateRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.GetRetrospectionResponse;
import com.ogd.stockdiary.application.retrospection.dto.response.MemoResponse;
import com.ogd.stockdiary.domain.principlecheck.dto.PrincipleCheckCommand;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheck;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.retrospection.entity.Memo;
import com.ogd.stockdiary.domain.retrospection.entity.Order;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.retrospection.port.in.GetRetrospectionCommand;
import com.ogd.stockdiary.domain.user.entity.User;

public class RetrospectionMapper {

    public static CreateRetrospectionCommand toCommand(
        CreateRetrospectionRequest request, Long userId) {
        List<PrincipleCheckCommand> principleCheckCommands = request.getPrincipleChecks() != null
            ? request.getPrincipleChecks().stream()
                .map(RetrospectionMapper::toPrincipleCheckCommand)
                .toList()
            : Collections.emptyList();

        return new CreateRetrospectionCommand(
            userId,
            request.getSymbol(),
            request.getMarket(),
            request.getOrderType(),
            request.getPrice(),
            request.getCurrency(),
            request.getVolume(),
            request.getOrderDate(),
            request.getReturnRate(),
            principleCheckCommands);
    }

    private static PrincipleCheckCommand toPrincipleCheckCommand(PrincipleCheckRequest request) {
        return new PrincipleCheckCommand(
            request.getPrincipleId(),
            request.getStatus(),
            request.getReason(),
            request.getImageIds(),
            request.getLinks());
    }

    public static CreateRetrospectionResponse toResponse(Retrospection retrospection) {
        return new CreateRetrospectionResponse(
            retrospection.getId(),
            retrospection.getUser().getId(),
            retrospection.getSymbol(),
            retrospection.getMarket(),
            retrospection.getOrder().getOrderType(),
            retrospection.getOrder().getPrice(),
            retrospection.getOrder().getCurrency(),
            retrospection.getOrder().getVolume(),
            retrospection.getOrder().getOrderDate(),
            retrospection.getReturnRate(),
            retrospection.getCreatedAt(),
            retrospection.getUpdatedAt());
    }

    public static Retrospection toEntity(CreateRetrospectionCommand command, User user) {
        Order order = new Order(
            command.getOrderType(),
            command.getPrice(),
            command.getCurrency(),
            command.getVolume(),
            command.getOrderDate());

        return new Retrospection(
            user,
            command.getSymbol(),
            command.getMarket(),
            order,
            command.getReturnRate());
    }

    public static GetRetrospectionResponse toGetResponse(
        Retrospection retrospection,
        List<PrincipleCheck> principleChecks,
        Map<Long, List<String>> imageUrlsMap,
        Map<Long, List<String>> linksMap,
        List<Memo> memos) {
        // PrincipleCheck를 PrincipleGroup으로 그룹핑
        Map<PrincipleGroup, List<PrincipleCheck>> groupedChecks = principleChecks.stream()
            .collect(Collectors.groupingBy(pc -> pc.getPrinciple().getPrincipleGroup()));

        // PrincipleGroupWithChecksResponse 리스트 생성 (groupId 순서로 정렬)
        List<GetRetrospectionResponse.PrincipleGroupWithChecksResponse> principleCheckGroups = groupedChecks.entrySet()
            .stream()
            .map(entry -> {
                PrincipleGroup group = entry.getKey();
                List<PrincipleCheck> checks = entry.getValue();

                // 그룹 내 원칙 체크들을 principleId 순서로 정렬하여 응답 생성
                List<GetRetrospectionResponse.PrincipleCheckResponse> checkResponses = checks.stream()
                    .sorted(Comparator.comparing(pc -> pc.getPrinciple().getId()))
                    .map(pc -> new GetRetrospectionResponse.PrincipleCheckResponse(
                        pc.getPrinciple().getId(),
                        pc.getPrinciple().getPrinciple(),
                        pc.getStatus(),
                        pc.getReason(),
                        imageUrlsMap.getOrDefault(pc.getId(), Collections.emptyList()),
                        linksMap.getOrDefault(pc.getId(), Collections.emptyList())))
                    .toList();

                return new GetRetrospectionResponse.PrincipleGroupWithChecksResponse(
                    group.getId(),
                    group.getGroupName(),
                    group.getThumbnail(),
                    group.getPrincipleType(),
                    checkResponses);
            })
            .sorted(Comparator.comparing(GetRetrospectionResponse.PrincipleGroupWithChecksResponse::getGroupId))
            .toList();

        List<MemoResponse> memoResponses = memos.stream()
            .map(MemoMapper::toResponse)
            .toList();

        return new GetRetrospectionResponse(
            retrospection.getId(),
            retrospection.getUser().getId(),
            retrospection.getSymbol(),
            retrospection.getMarket(),
            retrospection.getOrder().getOrderType(),
            retrospection.getOrder().getPrice(),
            retrospection.getOrder().getCurrency(),
            retrospection.getOrder().getVolume(),
            retrospection.getOrder().getOrderDate(),
            retrospection.getReturnRate(),
            principleCheckGroups,
            memoResponses,
            retrospection.getCreatedAt(),
            retrospection.getUpdatedAt());
    }

    public static GetRetrospectionCommand toCommand(Long userId) {
        return new GetRetrospectionCommand(
            userId);
    }

}
