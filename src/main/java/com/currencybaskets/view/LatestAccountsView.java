package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class LatestAccountsView {
    private List<AccountView> accounts;
    private Set<RateView> rates;
    private Date latestRatesUpdated;
    private BigDecimal totalAmount;
}
