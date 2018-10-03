package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.AggregatedAmount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    @Query("SELECT a FROM Account a " +
            "WHERE a.id IN " +
            "(SELECT MAX(a.id) from Account a " +
            "WHERE a.user.id IN ?1 " +
            "GROUP BY a.bank, a.currency.id, a.user.id)")
    List<Account> findLatestAccountByUserIds(List<Long> userIds);

    @Query("SELECT new com.currencybaskets.dao.model.AggregatedAmount(a.currency, SUM(a.amountBase))" +
            "FROM Account a " +
            "WHERE a.id IN " +
            "   (SELECT MAX(a.id) from Account a " +
            "   WHERE a.user.id IN ?1 " +
            "   GROUP BY a.bank, a.currency.id, a.user.id)" +
            "GROUP BY a.currency.id")
    List<AggregatedAmount> aggregateCurrencyForLatestAccountsByUserIds(List<Long> userIds);

    @Query("SELECT a FROM Account a " +
            "WHERE a.id IN " +
            "(SELECT MAX(a.id) from Account a " +
            "WHERE a.rate.id = ?1 " +
            "GROUP BY a.bank, a.currency.id, a.user.id)")
    List<Account> findLatestAccountByRateId(Long rateId);

    @Query("SELECT SUM(a.amountBase) FROM Account a " +
            "WHERE a.id IN " +
            "(SELECT MAX(a.id) from Account a " +
            "WHERE a.user.id IN ?1 AND a.updated <= ?2 " +
            "GROUP BY a.bank, a.currency.id, a.user.id)")
    BigDecimal sumOfBaseAmountsForUserIdsOnDate(List<Long> userIds, Date date);

    @Query("SELECT a FROM Account a " +
            "WHERE a.user.id IN ?1 AND a.updated > ?2")
    List<Account> findAccountsForUserIdsAfterDate(List<Long> userIds, Date date);

    List<Account> findByUserIdIn(List<Long> ids);
}
