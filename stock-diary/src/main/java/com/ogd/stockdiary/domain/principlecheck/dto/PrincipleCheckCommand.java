package com.ogd.stockdiary.domain.principlecheck.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrincipleCheckCommand {

  private final Long principleId;
  private final Boolean isFollowed;
}
