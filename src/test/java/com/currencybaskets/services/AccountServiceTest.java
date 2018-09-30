package com.currencybaskets.services;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Currency;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.model.User;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.dto.AmountHistoryDto;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.RateUpdate;
import com.currencybaskets.view.RateView;
import org.hamcrest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class AccountServiceTest {

    private static final String CURRENCY_NAME = "test_currency";

    private static Date LATEST_DATE = new GregorianCalendar(2017, Calendar.FEBRUARY, 11).getTime();
    private static Date NOT_LATEST_DATE = new GregorianCalendar(2017, Calendar.JANUARY, 11).getTime();
    private static Date NOT_NOT_LATEST_DATE = new GregorianCalendar(2016, Calendar.JANUARY, 11).getTime();

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

    @MockBean
    private RateRepository rateRepository;

    @Test
    public void getUserLatestAccounts() throws Exception {
        List<Long> userIds = Collections.singletonList(1L);
        when(repository.findLatestAccountByUserIds(userIds)).thenReturn(stubbedAccounts());
        when(repository.sumOfBaseAmountsForUserIdsOnDate(any(), any())).thenReturn(new BigDecimal(1000));
        LatestAccountsView actual = service.getUserLatestAccounts(userIds);
        assertTrue(
                actual.getAccounts()
                        .stream()
                        .map(AccountView::getCurrency)
                        .allMatch(CURRENCY_NAME::equals)
        );

        assertTrue(
                actual.getAccounts()
                        .stream()
                        .map(AccountView::getUserFullName)
                        .allMatch("n1 sn1"::equals)
        );

        assertAccountViewRates(
                actual.getAccounts()
                        .stream()
                        .map(AccountView::getRate)
                        .collect(Collectors.toList())
        );
        assertThat(actual.getTotalAmount(),  Matchers.comparesEqualTo(new BigDecimal(1111)));

        assertThat(actual.getWeekBaseAmountChange().getChange(),  Matchers.comparesEqualTo(new BigDecimal(111)));
        assertThat(actual.getWeekBaseAmountChange().getBackground(),  is("bg-success"));
        assertThat(actual.getWeekBaseAmountChange().getIcon(),  is("fa-long-arrow-up"));

        assertThat(actual.getMonthBaseAmountChange().getChange(),  Matchers.comparesEqualTo(new BigDecimal(111)));
        assertThat(actual.getMonthBaseAmountChange().getBackground(),  is("bg-success"));
        assertThat(actual.getMonthBaseAmountChange().getIcon(),  is("fa-long-arrow-up"));

        Set<RateView> actualRates = actual.getRates();
        assertEquals(actualRates.size(), 1);
        assertTrue(actualRates.stream().noneMatch(Objects::isNull));
        assertEquals(actual.getLatestRatesUpdated(), LATEST_DATE);
    }

    private static List<Account> stubbedAccounts() {
        Rate rate = rate(NOT_LATEST_DATE);
        Rate rateLatest = rate(LATEST_DATE);
        Rate rateNullUpdated = rate(null);
        Currency cur = rate.getCurrency();
        User usr = new User();
        usr.setName("n1");
        usr.setSurname("sn1");
        return Arrays.asList(
                stubbedAccount(usr, cur, rate, 1),
                stubbedAccount(usr, cur, null, 10),
                stubbedAccount(usr, cur, rateNullUpdated, 100),
                stubbedAccount(usr, cur, rateLatest, 1000)
        );
    }


    private static Rate rate(Date updated) {
        Currency cur = new Currency();
        cur.setName(CURRENCY_NAME);
        Rate rate = new Rate();
        rate.setCurrency(cur);
        rate.setRate(new BigDecimal(7.31));
        rate.setUpdated(updated);
        rate.setVersion(1);
        return rate;
    }

    private static Account stubbedAccount(User user, Currency currency, Rate rate, int baseAmount) {
        Account account = new Account();
        account.setCurrency(currency);
        account.setRate(rate);
        account.setUser(user);
        account.setAmountBase(new BigDecimal(baseAmount));
        return account;
    }

    private static void assertAccountViewRates(List<BigDecimal> rates) {
        assertThat(rates.size(), is(4));
        assertTrue(Stream.of(rates.get(0), rates.get(2), rates.get(3))
                .allMatch(rate -> isEqualWithDelta(rate, 7.31))
        );
        assertThat(rates.get(1), Matchers.comparesEqualTo(BigDecimal.ONE));
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
                           && isEqualWithDelta(incremental.getAmountBase(), 511.69)
                           && isEqualWithDelta(incremental.getAmountBaseChange(), 365.49);
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
        Rate rate = rate(LATEST_DATE);
        Account account = new Account();
        account.setId(1L);
        account.setCurrency(rate.getCurrency());
        account.setRate(rate);
        account.setVersion(1);
        account.setAmount(new BigDecimal(20));
        account.setAmountBase(new BigDecimal(146.2));
        return account;
    }

    @Test
    public void updateAccountsRate() throws Exception {
        long previousRateId = 1L;
        BigDecimal newRate = new BigDecimal(2.2);
        RateUpdate update = new RateUpdate();
        update.setId(previousRateId);
        update.setRate(newRate);
        Rate previousRate = rate(LATEST_DATE);
        when(rateRepository.findOne(previousRateId)).thenReturn(previousRate);
        Rate updatedRate = new Rate();
        updatedRate.setVersion(2);
        updatedRate.setRate(newRate);
        Currency cur = new Currency();
        cur.setName(CURRENCY_NAME);
        when(rateRepository.save(any(Rate.class))).thenReturn(updatedRate);
        when(repository.findLatestAccountByRateId(previousRateId))
                .thenReturn(Collections.singletonList(previousAccount()));
        service.updateAccountsRate(update);
        verify(repository, atLeastOnce()).save(argThat(isRateUpdated(newRate)));
    }

    private static Matcher<Iterable<Account>> isRateUpdated(BigDecimal newRate) {
        return new BaseMatcher<Iterable<Account>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                Iterator<Account> iterator = ((Iterable<Account>) item).iterator();
                if (!iterator.hasNext()) {
                    return false;
                }
                Account account = iterator.next();
                return account.getVersion().equals(2)
                        && account.getRate().getRate().equals(newRate)
                        && isEqualWithDelta(account.getAmount(), 20)
                        && isEqualWithDelta(account.getAmountChange(), 0)
                        && isEqualWithDelta(account.getAmountBase(), 44)
                        && isEqualWithDelta(account.getAmountBaseChange(), -102.2);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Rates must be update.");
            }
        };
    }

    private static boolean isEqualWithDelta(BigDecimal expected, double actual) {
       return expected.subtract(new BigDecimal(actual)).abs().floatValue() <= 0.1;
    }

    @Test
    public void getAggregatedAmountHistory() throws Exception {
        when(repository.sumOfBaseAmountsForUserIdsOnDate(any(), any())).thenReturn(new BigDecimal(500));
        when(repository.findAccountsForUserIdsAfterDate(any(), any())).thenReturn(updates());
        List<AmountHistoryDto> result = service.getAggregatedAmountHistory(Collections.singletonList(1L), NOT_NOT_LATEST_DATE);
        assertThat(result.size(), is(3));
        assertAmountHistory(result.get(0), 500.0, NOT_NOT_LATEST_DATE);
        assertAmountHistory(result.get(1), 1480.0, NOT_LATEST_DATE);
        assertAmountHistory(result.get(2), 1530.0, LATEST_DATE);

    }

    private static List<Account> updates() {
        Rate rate = rate(NOT_LATEST_DATE);
        Currency cur = rate.getCurrency();
        User usr = new User();
        usr.setName("n1");
        usr.setSurname("sn1");
        return Arrays.asList(
                update(usr, cur, rate, 1000, NOT_LATEST_DATE),
                update(usr, cur, rate, -20, NOT_LATEST_DATE),
                update(usr, cur, rate, 50, LATEST_DATE)
        );
    }

    private static Account update(User user, Currency currency, Rate rate, int amountChange, Date date) {
        Account account = new Account();
        account.setCurrency(currency);
        account.setRate(rate);
        account.setUser(user);
        account.setAmountBaseChange(new BigDecimal(amountChange));
        account.setUpdated(date);
        return account;
    }

    private static void assertAmountHistory(AmountHistoryDto subject, double amount, Date date) {
        assertThat(subject.getLabel(), is(AmountHistoryDto.LABEL_DATE_FORMATTER.format(date)));
        assertTrue(isEqualWithDelta(subject.getAmount(), amount));
    }
}