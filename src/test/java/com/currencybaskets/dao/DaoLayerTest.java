package com.currencybaskets.dao;

import com.currencybaskets.dao.model.*;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dao.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
public class DaoLayerTest {

    private static final BigDecimal R2_RATE = new BigDecimal(43);
    private static final String C1_NAME = "C1";
    private static final String C2_NAME = "C2";
    private static final BigDecimal AMOUNT_BASE = new BigDecimal(10.5);
    private static final BigDecimal AMOUNT_BASE_PREVIOUS = new BigDecimal(5.5);
    private static final long GROUP_ID = 2L;
    private static final ZonedDateTime NOW = ZonedDateTime.now();

    private Long userId1;
    private Long userId2;
    private Long rate2Id;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void createAccountsForAggregations() throws Exception {
        // Create user
        User usr1 = new User();
        usr1.setName("n1");
        usr1.setSurname("sn1");
        usr1.setGroupId(GROUP_ID);
        usr1.setColor("c1");
        userId1 = entityManager.persistAndGetId(usr1, Long.class);

        User usr2 = new User();
        usr2.setName("n2");
        usr2.setSurname("sn2");
        usr2.setGroupId(GROUP_ID);
        usr2.setColor("c2");
        userId2 = entityManager.persistAndGetId(usr2, Long.class);

        User usr3 = new User();
        usr3.setName("n2");
        usr3.setSurname("sn2");
        usr3.setGroupId(GROUP_ID + 1);
        usr3.setColor("c2");
        entityManager.persist(usr3);

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
        rate2Id = entityManager.persistAndGetId(r2, Long.class);

        String b1 = "b1";
        String b2 = "b2";


        Date monthAgo = Date.from(NOW.minusMonths(1).toInstant());
        Date weekAgo = Date.from(NOW.minusWeeks(1).toInstant());
        Date weekAndDay = Date.from(NOW.minusWeeks(1).minusDays(1).toInstant());

        createAndPersistAccount(c1, b1, r1, usr1, monthAgo, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c1, b1, r2, usr1, weekAndDay, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c1, b1, r2, usr1, weekAgo, AMOUNT_BASE);

        createAndPersistAccount(c1, b2, r1, usr1, monthAgo, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c1, b2, r2, usr1, weekAgo, AMOUNT_BASE);

        createAndPersistAccount(c2, b1, r1, usr1, monthAgo, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c2, b1, r2, usr1, weekAgo, AMOUNT_BASE);

        createAndPersistAccount(c2, b2, r1, usr1, monthAgo, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c2, b2, r2, usr1, weekAgo, AMOUNT_BASE);

        createAndPersistAccount(c2, b2, r1, usr2, monthAgo, AMOUNT_BASE_PREVIOUS);
        createAndPersistAccount(c2, b2, r2, usr2, weekAgo, AMOUNT_BASE);
    }

    private void createAndPersistAccount(Currency currency, String bank,
                                         Rate rate, User user, Date date, BigDecimal base) {
        // Use all in account
        Account account = new Account();
        account.setAmount(new BigDecimal(10.5));
        account.setBank(bank);
        account.setAmountBase(base);
        account.setUpdated(new Date());
        account.setPreviousId(-1L);
        account.setVersion(1);
        account.setUser(user);
        account.setCurrency(currency);
        account.setRate(rate);
        account.setUpdated(date);
        entityManager.persist(account);
    }

    private List<Long> userIds() {
        return Arrays.asList(userId1, userId2);
    }

    @Test
    public void findLatestAccountByUserId() throws Exception {
        List<Account> latestAccountByUserId = accountRepository.findLatestAccountByUserIds(userIds());
        assertEquals(latestAccountByUserId.size(), 5);
        // in pair only second rate should be returned by query because it used in latest versions
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
        List<AggregatedAmount> aggregatedAmounts = accountRepository.aggregateCurrencyForLatestAccountsByUserIds(userIds());
        assertEquals(aggregatedAmounts.size(), 2);
        // we have two accounts with different banks for currency1
        BigDecimal sum = AMOUNT_BASE.add(AMOUNT_BASE);
        AggregatedAmount c1Expected = getByCurrencyName(aggregatedAmounts, C1_NAME);
        AggregatedAmount c2Expected = getByCurrencyName(aggregatedAmounts, C2_NAME);
        assertThat(c1Expected.getAmount(),  Matchers.comparesEqualTo(sum));
        // we have three accounts with different banks and users for currency2
        assertThat(c2Expected.getAmount(),  Matchers.comparesEqualTo(sum.add(AMOUNT_BASE)));
    }

    private static AggregatedAmount getByCurrencyName(List<AggregatedAmount> aggregatedAmounts,
                                                      String name) {
       return aggregatedAmounts.stream()
               .filter(element -> name.equals(element.getCurrency().getName()))
               .findFirst()
               .orElseThrow(() -> new AssertionError("No Currency " + name));
    }

    @Test
    public void testGetUserIdInSameGroup() throws Exception {
        List<Long> userIds = userRepository.getUserIdsInSameGroup(userId1);
        assertThat(userIds, containsInAnyOrder(userId1, userId2));
    }

    @Test
    public void testFindLatestAccountByRateId() throws Exception {
        List<Account> actual = accountRepository.findLatestAccountByRateId(rate2Id);
        assertEquals(actual.size(), 5);
    }

    @Test
    public void testSumOfBaseAmountsForUserIdsOnDate() throws Exception {
        BigDecimal monthSum = accountRepository.sumOfBaseAmountsForUserIdsOnDate(userIds(),
                Date.from(NOW.minusMonths(1).toInstant()));
        assertThat(monthSum, Matchers.comparesEqualTo(AMOUNT_BASE_PREVIOUS.multiply(new BigDecimal(5))));

        BigDecimal weekSum = accountRepository.sumOfBaseAmountsForUserIdsOnDate(userIds(),
                Date.from(NOW.minusDays(6).toInstant()));
        assertThat(weekSum, Matchers.comparesEqualTo(AMOUNT_BASE.multiply(new BigDecimal(5))));

        BigDecimal nullSum = accountRepository.sumOfBaseAmountsForUserIdsOnDate(userIds(),
                Date.from(NOW.minusMonths(3).toInstant()));
        assertTrue(Objects.isNull(nullSum));
    }

}