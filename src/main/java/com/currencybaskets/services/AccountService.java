package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.dto.AggregatedAmountDto;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.RateView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService {

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

    @Transactional
    public void updateAccountAmount(AccountUpdate update) {
        Account previous = accountRepository.findOne(update.getId());
        if (previous != null) {
            Account incrementalUpdate = createIncrementalUpdate(previous, new BigDecimal(update.getAmount()));
            accountRepository.save(incrementalUpdate);
        } else {
            // TODO throw error and handle it properly
            log.error("Not found account for id={}", update.getId());
        }
    }

    private static Account createIncrementalUpdate(Account previous, BigDecimal newAmount) {
        Account updated = new Account();
        updated.setBank(previous.getBank());
        updated.setCurrency(previous.getCurrency());
        updated.setUser(previous.getUser());
        Rate rate = previous.getRate();
        updated.setRate(rate);
        updated.setPreviousId(previous.getId());
        updated.setVersion(previous.getVersion() + 1);
        updated.setAmount(newAmount);
        updated.setAmountChange(newAmount.subtract(previous.getAmount()));
        BigDecimal newAmountBase = Objects.nonNull(rate)
                ? newAmount.multiply(rate.getRate())
                : newAmount;
        updated.setAmountBase(newAmountBase);
        updated.setAmountBaseChange(newAmountBase.subtract(previous.getAmountBase()));
        updated.setUpdated(new Date());
        return updated;
    }
}
