package com.ogd.stockdiary.application.stock.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceOutput1 {

    @JsonProperty("rsym")
    private String symbol;

    @JsonProperty("zdiv")
    private String division;

    @JsonProperty("nrec")
    private String recordCount;
}