package com.ogd.stockdiary.application.principlegroup.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ogd.stockdiary.application.investmentprinciple.dto.mapper.InvestmentPrincipleMapper;
import com.ogd.stockdiary.application.investmentprinciple.dto.response.InvestmentPrincipleResponse;
import com.ogd.stockdiary.application.principlegroup.dto.request.CreatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.ReorderPrincipleGroupsRequest;
import com.ogd.stockdiary.application.principlegroup.dto.request.UpdatePrincipleGroupRequest;
import com.ogd.stockdiary.application.principlegroup.dto.response.PrincipleGroupResponse;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;
import com.ogd.stockdiary.domain.principlegroup.dto.CreatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.ReorderPrincipleGroupsCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.UpdatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;

@Component
public class PrincipleGroupMapper {

    public CreatePrincipleGroupCommand toCommand(CreatePrincipleGroupRequest request, Long userId) {
        List<CreatePrincipleGroupCommand.PrincipleItem> commandItems = null;
        if (request.getPrinciples() != null) {
            commandItems = request.getPrinciples().stream()
                .map(item -> new CreatePrincipleGroupCommand.PrincipleItem(
                    item.getPrinciple(), item.getDescription()))
                .collect(Collectors.toList());
        }
        return new CreatePrincipleGroupCommand(
            userId, request.getGroupName(), request.getDisplayOrder(), request.getPrincipleType(),
            request.getThumbnail(), commandItems);
    }

    public UpdatePrincipleGroupCommand toCommand(
        UpdatePrincipleGroupRequest request, Long groupId, Long userId) {
        return new UpdatePrincipleGroupCommand(groupId, userId, request.getGroupName(),
            request.getPrincipleType(), request.getThumbnail());
    }

    public ReorderPrincipleGroupsCommand toReorderCommand(
        ReorderPrincipleGroupsRequest request, Long userId) {
        List<ReorderPrincipleGroupsCommand.GroupOrder> groupOrders = request.getGroupOrders().stream()
            .map(
                order -> new ReorderPrincipleGroupsCommand.GroupOrder(
                    order.getGroupId(), order.getDisplayOrder()))
            .collect(Collectors.toList());

        return new ReorderPrincipleGroupsCommand(userId, groupOrders);
    }

    public PrincipleGroupResponse toResponse(
        PrincipleGroup principleGroup, List<InvestmentPrinciple> principles) {
        List<InvestmentPrincipleResponse> principleResponses = principles.stream()
            .map(InvestmentPrincipleMapper::toResponse)
            .collect(Collectors.toList());

        return new PrincipleGroupResponse(
            principleGroup.getId(),
            principleGroup.getGroupName(),
            principleGroup.getPrincipleType(),
            principleGroup.getThumbnail(),
            principleGroup.getDisplayOrder(),
            principleResponses);
    }
}
