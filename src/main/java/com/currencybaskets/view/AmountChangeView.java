package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AmountChangeView {
    private BigDecimal change;
    private String background;
    private String icon;
}
