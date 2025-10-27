package com.ogd.stockdiary.application.investmentprinciple.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.ogd.stockdiary.application.investmentprinciple.dto.request.BatchProcessRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.CreateMultiplePrinciplesRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.CreatePrincipleRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.ReorderPrinciplesRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.request.UpdatePrincipleRequest;
import com.ogd.stockdiary.application.investmentprinciple.dto.response.BatchProcessResponse;
import com.ogd.stockdiary.application.investmentprinciple.dto.response.InvestmentPrincipleResponse;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.BatchProcessResult;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreateMultiplePrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.CreatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.ReorderPrinciplesCommand;
import com.ogd.stockdiary.domain.investmentprinciple.dto.UpdatePrincipleCommand;
import com.ogd.stockdiary.domain.investmentprinciple.entity.InvestmentPrinciple;

public class InvestmentPrincipleMapper {

    public static CreatePrincipleCommand toCommand(CreatePrincipleRequest request, Long userId) {
        return new CreatePrincipleCommand(
            userId, request.getGroupId(), request.getPrinciple(), request.getDisplayOrder());
    }

    public static CreateMultiplePrinciplesCommand toCommand(
        CreateMultiplePrinciplesRequest request, Long userId) {
        return new CreateMultiplePrinciplesCommand(userId, request.getPrinciples());
    }

    public static UpdatePrincipleCommand toCommand(
        UpdatePrincipleRequest request, Long principleId, Long userId) {
        return new UpdatePrincipleCommand(principleId, userId, request.getPrinciple());
    }

    public static BatchProcessCommand toCommand(BatchProcessRequest request, Long userId) {
        List<UpdatePrincipleCommand> updateCommands = null;
        if (request.getUpdatePrinciples() != null) {
            updateCommands = request.getUpdatePrinciples().stream()
                .map(
                    update -> new UpdatePrincipleCommand(
                        update.getPrincipleId(),
                        userId,
                        update.getPrinciple()))
                .collect(Collectors.toList());
        }

        return new BatchProcessCommand(
            userId,
            request.getCreatePrinciples(),
            updateCommands,
            request.getDeletePrincipleIds());
    }

    public static InvestmentPrincipleResponse toResponse(InvestmentPrinciple principle) {
        Long groupId = principle.getPrincipleGroup() != null
            ? principle.getPrincipleGroup().getId()
            : null;
        String groupName = principle.getPrincipleGroup() != null
            ? principle.getPrincipleGroup().getGroupName()
            : null;
        return new InvestmentPrincipleResponse(
            principle.getId(), groupId, groupName, principle.getPrinciple(), principle.getDisplayOrder());
    }

    public static List<InvestmentPrincipleResponse> toResponseList(
        List<InvestmentPrinciple> principles) {
        return principles.stream()
            .map(InvestmentPrincipleMapper::toResponse)
            .collect(Collectors.toList());
    }

    public static BatchProcessResponse toBatchResponse(BatchProcessResult result) {
        return new BatchProcessResponse(
            toResponseList(result.getCreatedPrinciples()),
            toResponseList(result.getUpdatedPrinciples()),
            result.getDeletedPrincipleIds());
    }

    public static ReorderPrinciplesCommand toReorderCommand(
        ReorderPrinciplesRequest request, Long userId) {
        List<ReorderPrinciplesCommand.PrincipleOrder> principleOrders = request.getPrincipleOrders().stream()
            .map(
                order -> new ReorderPrinciplesCommand.PrincipleOrder(
                    order.getPrincipleId(), order.getDisplayOrder()))
            .collect(Collectors.toList());

        return new ReorderPrinciplesCommand(userId, principleOrders);
    }
}
