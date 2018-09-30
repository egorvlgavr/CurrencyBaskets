package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.dto.AggregatedAmountDto;
import com.currencybaskets.dto.AmountHistoryDto;
import com.currencybaskets.view.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RateRepository rateRepository;

    public LatestAccountsView getUserLatestAccounts(List<Long> userIds) {
        List<Account> latestAccountByUserId = accountRepository.findLatestAccountByUserIds(userIds);
        List<AccountView> accountViews = new ArrayList<>(latestAccountByUserId.size());
        Set<RateView> rates = new HashSet<>();
        Date latestRatesUpdated = null;
        BigDecimal totalBase = BigDecimal.ZERO;
        for (Account account : latestAccountByUserId) {
            accountViews.add(AccountView.fromEntity(account));
            Rate rate = account.getRate();
            if (Objects.nonNull(rate)) {
                rates.add(RateView.fromEntity(rate));

                Date updated = rate.getUpdated();
                if (Objects.nonNull(updated)) {
                    if (Objects.isNull(latestRatesUpdated)) {
                        latestRatesUpdated = updated;
                    } else {
                        latestRatesUpdated = latest(updated, latestRatesUpdated);
                    }
                } else {
                    log.warn("Rate={} with null \"updated\" field", rate.getId());
                }

            }
            totalBase = totalBase.add(account.getAmountBase());
        }
        ZonedDateTime now = ZonedDateTime.now();
        Date monthAgo = Date.from(now.minusMonths(1).toInstant());
        Date weekAgo = Date.from(now.minusWeeks(1).toInstant());

        return LatestAccountsView.builder()
                .accounts(accountViews)
                .rates(rates)
                .latestRatesUpdated(latestRatesUpdated)
                .totalAmount(totalBase)
                .weekBaseAmountChange(findAmountBaseChange(totalBase, userIds, weekAgo))
                .monthBaseAmountChange(findAmountBaseChange(totalBase, userIds, monthAgo))
                .build();
    }

    private AmountChangeView findAmountBaseChange(BigDecimal currentAmount, List<Long> userIds, Date from) {
        BigDecimal previousAmount = accountRepository.sumOfBaseAmountsForUserIdsOnDate(userIds, from);
        BigDecimal change = Objects.nonNull(previousAmount) ? currentAmount.subtract(previousAmount) : BigDecimal.ZERO;
        String background = attributeBasedOnChange(change, "bg-success", "bg-danger", "bg-primary");
        String icon = attributeBasedOnChange(change, "fa-long-arrow-up", "fa-long-arrow-down", "fa-ban");
        return new AmountChangeView(change, background, icon);
    }

    private static String attributeBasedOnChange(BigDecimal value, String possitive,
                                          String negative, String zero) {
        String result;
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            result = possitive;
        } else if (value.compareTo(BigDecimal.ZERO) < 0) {
            result = negative;
        } else {
            result = zero;
        }
        return result;
    }

    private static Date latest(Date left, Date right) {
        return left.after(right) ? left : right;
    }

    public List<AggregatedAmountDto> getAggregatedAmount(List<Long> userIds) {
        return accountRepository.aggregateCurrencyForLatestAccountsByUserIds(userIds)
                .stream()
                .map(AggregatedAmountDto::fromEntity)
                .collect(toList());
    }

    @Transactional
    public void updateAccountAmount(AccountUpdate update) {
        Account previous = accountRepository.findOne(update.getId());
        if (previous != null) {
            Account incrementalUpdate = previous.createAccountAmountUpdate(new BigDecimal(update.getAmount()));
            accountRepository.save(incrementalUpdate);
            log.debug("Update account with id={} on amount={}", previous.getId(), update.getAmount());
        } else {
            // TODO throw exception and handle it properly
            log.error("Not found account for id={}", update.getId());
        }
    }

    @Transactional
    public void updateAccountsRate(RateUpdate update) {
        Rate previousRate = rateRepository.findOne(update.getId());
        if (Objects.isNull(previousRate)) {
            // TODO throw exception and handle it properly
            log.error("No rate with id={}", update.getId());
            return;
        }
        Rate rate = rateRepository.save(previousRate.createRateUpdate(update.getRate()));
        List<Account> accountsToUpdate = accountRepository.findLatestAccountByRateId(update.getId());
        if (accountsToUpdate.isEmpty()) {
            log.warn("No accounts with rate id={}", update.getId());
            return;
        }
        List<Account> updatedAccounts = accountsToUpdate.stream()
                .peek(value -> log.debug("Update account with id={} on rate={}", value.getId(), update.getRate()))
                .map(toUpdate -> toUpdate.createAccountRateUpdate(rate))
                .collect(toList());
        accountRepository.save(updatedAccounts);
    }

    @Transactional
    public List<AmountHistoryDto> getAggregatedAmountHistory(List<Long> userIds, Date from) {
        BigDecimal aggregatedAmount = accountRepository.sumOfBaseAmountsForUserIdsOnDate(userIds, from);
        if (Objects.isNull(aggregatedAmount)) {
            aggregatedAmount = BigDecimal.ZERO;
        }
        List<AmountHistoryDto> result = new ArrayList<>();
        result.add(new AmountHistoryDto(from, aggregatedAmount));

        List<Account> updates = accountRepository.findAccountsForUserIdsAfterDate(userIds, from);
        TreeMap<Date, List<Account>> groupedByDateAccounts = updates.stream()
                .collect(groupingBy(Account::getUpdated, TreeMap::new, toList()));
        for (Map.Entry<Date, List<Account>> dateToAccounts : groupedByDateAccounts.entrySet()) {
            aggregatedAmount = aggregatedAmount.add(calculateAggregatedAmountChanges(dateToAccounts.getValue()));
            result.add(new AmountHistoryDto(dateToAccounts.getKey(), aggregatedAmount));
        }
        return result;
    }

    private static BigDecimal calculateAggregatedAmountChanges(Collection<Account> accounts) {
       return accounts.stream().map(Account::getAmountBaseChange)
           .filter(Objects::nonNull)
           .reduce(BigDecimal.ZERO, BigDecimal::add)
           .round(new MathContext(2));
    }
}
