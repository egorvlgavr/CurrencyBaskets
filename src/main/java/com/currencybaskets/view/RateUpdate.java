package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateUpdate {
    private Long id;
    private BigDecimal rate;
}
