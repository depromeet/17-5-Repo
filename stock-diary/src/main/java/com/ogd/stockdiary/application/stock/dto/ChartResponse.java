package com.ogd.stockdiary.application.stock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponse {

  private String status;
  private ChartData data;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChartData {
    private String currency;
    private List<ChartItem> chartData;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChartItem {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
  }
}
