package com.ogd.stockdiary.domain.stock.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockChartData {

  private String currency;
  private List<StockChartItem> chartData;
}
