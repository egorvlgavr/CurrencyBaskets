package com.currencybaskets.dao;

import com.currencybaskets.dao.model.*;
import com.currencybaskets.dao.repository.AccountRepository;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DaoLayerTest {

    private static final BigDecimal R2_RATE = new BigDecimal(43);
    private static final String C1_NAME = "C1";
    private static final String C2_NAME = "C2";
    private static final BigDecimal AMOUNT_BASE = new BigDecimal(10.5);

    private Long id;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository repository;

    @Before
    public void createAccountsForAggregations() throws Exception {
        // Create user
        User usr1 = new User();
        usr1.setName("n");
        usr1.setSurname("sn");
        usr1.setGroupId(2L);
        usr1.setColor("c");
        id = entityManager.persistAndGetId(usr1, Long.class);

        // Create currency
        Currency c1 = new Currency();
        c1.setName(C1_NAME);
        entityManager.persist(c1);

        Currency c2 = new Currency();
        c2.setName(C2_NAME);
        entityManager.persist(c2);

        // Create rate
        Rate r1 = new Rate();
        r1.setCurrency(c1);
        r1.setVersion(0);
        r1.setRate(new BigDecimal(42));
        r1.setUpdated(new Date());
        entityManager.persist(r1);

        Rate r2 = new Rate();
        r2.setCurrency(c1);
        r2.setVersion(0);
        r2.setRate(R2_RATE);
        r2.setUpdated(new Date());
        entityManager.persist(r2);

        String b1 = "b1";
        String b2 = "b2";
        // in pair only second element should be returned by query
        createAndPersistAccount(c1, b1, r1, usr1);
        createAndPersistAccount(c1, b1, r2, usr1);

        createAndPersistAccount(c1, b2, r1, usr1);
        createAndPersistAccount(c1, b2, r2, usr1);

        createAndPersistAccount(c2, b1, r2, usr1);
        createAndPersistAccount(c2, b1, r2, usr1);

        createAndPersistAccount(c2, b2, r2, usr1);
        createAndPersistAccount(c2, b2, r2, usr1);
    }

    private void createAndPersistAccount(Currency currency, String bank,
                                         Rate rate, User user) {
        // Use all in account
        Account account = new Account();
        account.setAmount(new BigDecimal(10.5));
        account.setBank(bank);
        account.setAmountBase(AMOUNT_BASE);
        account.setUpdated(new Date());
        account.setPreviousId(-1L);
        account.setVersion(1);
        account.setUser(user);
        account.setCurrency(currency);
        account.setRate(rate);
        entityManager.persist(account);
    }

    @Test
    public void findLatestAccountByUserId() throws Exception {
        List<Account> latestAccountByUserId = repository.findLatestAccountByUserId(id);
        assertEquals(latestAccountByUserId.size(), 4);
        assertTrue(
                latestAccountByUserId
                        .stream()
                        .map(Account::getRate)
                        .map(Rate::getRate)
                        .allMatch(R2_RATE::equals)
        );
    }

    @Test
    public void aggregateCurrencyForLatestAccountsByUserId() throws Exception {
        List<AggregatedAmount> aggregatedAmounts = repository.aggregateCurrencyForLatestAccountsByUserId(id);
        assertEquals(aggregatedAmounts.size(), 2);
        // we have two account with different banks and same currency
        BigDecimal sum = AMOUNT_BASE.add(AMOUNT_BASE);
        AggregatedAmount c1Expected = getByCurrencyName(aggregatedAmounts, C1_NAME);
        AggregatedAmount c2Expected = getByCurrencyName(aggregatedAmounts, C2_NAME);
        assertThat(c1Expected.getAmount(),  Matchers.comparesEqualTo(sum));
        assertThat(c2Expected.getAmount(),  Matchers.comparesEqualTo(sum));
    }

    private static AggregatedAmount getByCurrencyName(List<AggregatedAmount> aggregatedAmounts,
                                                      String name) {
       return aggregatedAmounts.stream()
               .filter(element -> name.equals(element.getCurrency().getName()))
               .findFirst()
               .orElseThrow(() -> new AssertionError("No Currency " + name));
    }

}