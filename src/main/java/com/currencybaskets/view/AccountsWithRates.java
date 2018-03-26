package com.currencybaskets.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class AccountsWithRates {
    private List<AccountView> accounts;
    private Set<RateView> rates;
}
