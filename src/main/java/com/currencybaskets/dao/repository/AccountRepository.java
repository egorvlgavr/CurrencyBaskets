package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
