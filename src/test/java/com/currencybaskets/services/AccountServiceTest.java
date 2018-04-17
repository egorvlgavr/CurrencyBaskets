package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Currency;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.RateView;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class AccountServiceTest {

    private static final String CURRENCY_NAME = "test_currency";

    @TestConfiguration
    static class AccountServiceTestConfiguration {
        @Bean
        AccountService accountService() {
            return new AccountService();
        }
    }

    @Autowired
    private AccountService service;

    @MockBean
    private AccountRepository repository;

    @Test
    public void getUserLatestAccounts() throws Exception {
        long userId = 1L;
        when(repository.findLatestAccountByUserId(userId)).thenReturn(stubbedAccounts());
        LatestAccountsView actual = service.getUserLatestAccounts(userId);
        assertTrue(
                actual.getAccounts()
                        .stream()
                        .map(AccountView::getCurrency)
                        .allMatch(CURRENCY_NAME::equals)
        );
        assertThat(actual.getTotalAmount(),  Matchers.comparesEqualTo(new BigDecimal(111)));
        Set<RateView> actualRates = actual.getRates();
        assertEquals(actualRates.size(), 1);
        assertTrue(actualRates.stream().noneMatch(Objects::isNull));
    }

    private static List<Account> stubbedAccounts() {
        Rate rate = rate();
        Currency cur = rate.getCurrency();
        return Arrays.asList(
                stubbedAccount(cur, rate, 1),
                stubbedAccount(cur, null, 10),
                stubbedAccount(cur, rate, 100)
        );
    }

    private static Rate rate() {
        Currency cur = new Currency();
        cur.setName(CURRENCY_NAME);
        Rate rate = new Rate();
        rate.setCurrency(cur);
        rate.setRate(new BigDecimal(7.31));
        return rate;
    }

    private static Account stubbedAccount(Currency currency, Rate rate, int baseAmount) {
        Account account = new Account();
        account.setCurrency(currency);
        account.setRate(rate);
        account.setAmountBase(new BigDecimal(baseAmount));
        return account;
    }

    @Test
    public void updateAccountAmount() throws Exception {
        long accountId = 1L;
        when(repository.findOne(accountId)).thenReturn(previousAccount());
        AccountUpdate update = new AccountUpdate();
        update.setAmount(70);
        update.setId(accountId);
        service.updateAccountAmount(update);
        verify(repository, atLeastOnce()).save(argThat(isIncremental()));
    }

    private static Matcher<Account> isIncremental() {
       return new BaseMatcher<Account>() {
           @Override
           public boolean matches(Object item) {
               if (item instanceof Account) {
                   Account incremental = (Account) item;
                   return  (incremental.getAmount().intValue() == 70)
                           && (incremental.getAmountChange().intValue() == 50)
                           && (incremental.getVersion() == 2)
                           && (incremental.getAmountBase()
                           .subtract(new BigDecimal(511.69)).abs().floatValue() <= 0.1)
                           && (incremental.getAmountBaseChange()
                           .subtract(new BigDecimal(365.49)).abs().floatValue() <= 0.1);
               }
               return false;
           }

           @Override
           public void describeTo(Description description) {
                description.appendText("Must be incremental!");
           }
       };
    }

    private static Account previousAccount() {
        Rate rate = rate();
        Account account = new Account();
        account.setId(1L);
        account.setCurrency(rate.getCurrency());
        account.setRate(rate);
        account.setVersion(1);
        account.setAmount(new BigDecimal(20));
        account.setAmountBase(new BigDecimal(146.2));
        return account;
    }
}