package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.AccountsWithRates;
import com.currencybaskets.view.RateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;


    public AccountsWithRates getUserLatestAccounts(Long userId) {
        // TODO group by (bank and currency) to avid incorrect aggregation
        List<Account> latestAccountByUserId = accountRepository.findLatestAccountByUserId(userId);
        List<AccountView> accountViews = new ArrayList<>(latestAccountByUserId.size());
        Set<RateView> rates = new HashSet<>();
        for (Account account : latestAccountByUserId) {
            accountViews.add(AccountView.fromEntity(account));
            rates.add(RateView.fromEntity(account.getRate()));
        }
        return new AccountsWithRates(accountViews, rates);
    }
}
