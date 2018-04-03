package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dto.AggregatedAmountDto;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.RateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private static final BigDecimal HUNDRED = new BigDecimal(100);

    @Autowired
    private AccountRepository accountRepository;


    public LatestAccountsView getUserLatestAccounts(Long userId) {
        List<Account> latestAccountByUserId = accountRepository.findLatestAccountByUserId(userId);
        List<AccountView> accountViews = new ArrayList<>(latestAccountByUserId.size());
        Set<RateView> rates = new HashSet<>();
        BigDecimal totalBase = new BigDecimal(0);
        for (Account account : latestAccountByUserId) {
            accountViews.add(AccountView.fromEntity(account));
            Rate rate = account.getRate();
            if (Objects.nonNull(rate)) {
                rates.add(RateView.fromEntity(rate));
            }
            totalBase = totalBase.add(account.getAmountBase());
        }
        return new LatestAccountsView(accountViews, rates, totalBase);
    }

    public List<AggregatedAmountDto> getAggregatedAmount(Long userId) {
        return accountRepository.aggregateCurrencyForLatestAccountsByUserId(userId)
                .stream()
                .map(AggregatedAmountDto::fromEntity)
                .collect(Collectors.toList());
    }
}
