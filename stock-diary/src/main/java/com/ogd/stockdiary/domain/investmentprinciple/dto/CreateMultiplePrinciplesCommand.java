package com.ogd.stockdiary.domain.investmentprinciple.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateMultiplePrinciplesCommand {

  private final Long userId;
  private final List<String> principles;
}
