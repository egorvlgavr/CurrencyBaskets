package com.currencybaskets.dao.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AggregatedAmount {
    private Currency currency;
    private BigDecimal amount;
}
