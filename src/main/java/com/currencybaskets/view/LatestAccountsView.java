package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LatestAccountsView {
    private List<AccountView> accounts;
    private Set<RateView> rates;
    private Date latestRatesUpdated;
    private BigDecimal totalAmount;
    private AmountChangeView weekBaseAmountChange;
    private AmountChangeView monthBaseAmountChange;
}
