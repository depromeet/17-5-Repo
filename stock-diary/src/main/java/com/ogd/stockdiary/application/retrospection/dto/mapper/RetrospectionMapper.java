package com.ogd.stockdiary.application.retrospection.dto.mapper;

import java.util.Collections;
import java.util.List;

import com.ogd.stockdiary.application.principlecheck.dto.request.PrincipleCheckRequest;
import com.ogd.stockdiary.application.retrospection.dto.request.CreateRetrospectionRequest;
import com.ogd.stockdiary.application.retrospection.dto.response.CreateRetrospectionResponse;
import com.ogd.stockdiary.domain.principlecheck.dto.PrincipleCheckCommand;
import com.ogd.stockdiary.domain.retrospection.entity.Order;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.in.CreateRetrospectionCommand;
import com.ogd.stockdiary.domain.user.entity.User;

public class RetrospectionMapper {

  public static CreateRetrospectionCommand toCommand(CreateRetrospectionRequest request, Long userId) {
    List<PrincipleCheckCommand> principleCheckCommands = request.getPrincipleChecks() != null
        ? request.getPrincipleChecks().stream().map(RetrospectionMapper::toPrincipleCheckCommand).toList()
        : Collections.emptyList();

    return new CreateRetrospectionCommand(userId, request.getSymbol(), request.getMarket(), request.getOrderType(),
        request.getPrice(), request.getCurrency(), request.getVolume(), request.getOrderDate(),
        request.getReturnRate(), request.getEmotion(), principleCheckCommands);
  }

  private static PrincipleCheckCommand toPrincipleCheckCommand(PrincipleCheckRequest request) {
    return new PrincipleCheckCommand(request.getPrincipleId(), request.getIsFollowed());
  }

  public static CreateRetrospectionResponse toResponse(Retrospection retrospection) {
    return new CreateRetrospectionResponse(retrospection.getId(), retrospection.getUser().getId(),
        retrospection.getSymbol(), retrospection.getMarket(), retrospection.getOrder().getOrderType(),
        retrospection.getOrder().getPrice(), retrospection.getOrder().getCurrency(),
        retrospection.getOrder().getVolume(), retrospection.getOrder().getOrderDate(),
        retrospection.getReturnRate(), retrospection.getEmotion(), retrospection.getCreatedAt(),
        retrospection.getUpdatedAt());
  }

  public static Retrospection toEntity(CreateRetrospectionCommand command, User user) {
    Order order = new Order(command.getOrderType(), command.getPrice(), command.getCurrency(), command.getVolume(),
        command.getOrderDate());

    return new Retrospection(user, command.getSymbol(), command.getMarket(), order, command.getReturnRate(),
        command.getEmotion());
  }
}
