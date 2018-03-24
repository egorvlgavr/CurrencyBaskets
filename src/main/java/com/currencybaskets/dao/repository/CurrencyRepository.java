package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {
}
