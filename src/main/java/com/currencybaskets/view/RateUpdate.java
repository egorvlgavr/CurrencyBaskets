package com.currencybaskets.view;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateUpdate {
    private Long id;
    private BigDecimal rate;
}
