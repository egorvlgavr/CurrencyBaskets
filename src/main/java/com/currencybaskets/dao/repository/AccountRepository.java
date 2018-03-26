package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    @Query("SELECT a FROM Account a " +
            "WHERE a.id IN " +
            "(SELECT MAX(a.id) from Account a " +
            "WHERE a.user.id = ?1 " +
            "GROUP BY a.bank)")
    List<Account> findLatestAccountByUserId(Long userId);
}
