package com.currencybaskets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
public class AmountHistoryDto {

  public static final Format LABEL_DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    private String label;
    private BigDecimal amount;

  public AmountHistoryDto(Date label, BigDecimal amount) {
    this.label = LABEL_DATE_FORMATTER.format(label);
    this.amount = amount;
  }
}
