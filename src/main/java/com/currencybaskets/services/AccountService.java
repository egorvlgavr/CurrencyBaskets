package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.AggregatedAmountView;
import com.currencybaskets.view.RateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

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
        Map<String, AggregatedAmountView> aggregated = aggregateBaseAmounts(accountViews, totalBase);
        return new LatestAccountsView(accountViews, rates, totalBase, aggregated);
    }

    private static Map<String, AggregatedAmountView> aggregateBaseAmounts(List<AccountView> accounts,
                                                                          BigDecimal total) {
        Map<String, BigDecimal> aggregated = accounts.stream()
                .collect(
                        groupingBy(AccountView::getCurrency,
                                reducing(new BigDecimal(0),
                                        AccountView::getAmountBase,
                                        BigDecimal::add
                                )
                        )
                );
        Map<String, AggregatedAmountView> result = new HashMap<>(aggregated.size());
        for (Map.Entry<String, BigDecimal> entry : aggregated.entrySet()) {
            BigDecimal percentage = entry.getValue()
                    .divide(total, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(HUNDRED);
            result.put(entry.getKey(), new AggregatedAmountView(entry.getValue(), percentage));
        }
        return result;
    }
}
