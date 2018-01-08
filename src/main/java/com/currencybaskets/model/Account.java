package com.currencybaskets.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class Account {
    private Long id;
    private String bank;
    private Integer userId;
    private Integer version;
    private Integer currencyId;
    private Long previousId;
    private Integer rateId;
    private BigDecimal amount;
    private BigDecimal amountChange;
    private BigDecimal amountBase;
    private BigDecimal amountBaseChange;
    private Date updated;
}
