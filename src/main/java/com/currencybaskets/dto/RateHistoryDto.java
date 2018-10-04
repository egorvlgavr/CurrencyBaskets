package com.currencybaskets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RateHistoryDto {
  private String currency;
  private String color;
  private List<HistoryDto> history;
}
