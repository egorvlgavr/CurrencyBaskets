package com.currencybaskets.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Rate {
    private Long id;
    private Integer currencyId;
    private Integer version;
    private Integer rateId;
    private Date updated;
    private BigDecimal rate;
}
