package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AggregatedAmountView {
    private BigDecimal amount;
    private BigDecimal percents;
}
