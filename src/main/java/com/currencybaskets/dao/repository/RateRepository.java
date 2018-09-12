package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Rate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RateRepository extends CrudRepository<Rate, Long> {
	@Query("SELECT MAX(r.id) FROM Rate r WHERE r.currency.id IN "
		+ "(SELECT r.currency.id FROM Rate r "
		+ "WHERE UPPER(r.currency.name) = UPPER(?1))")
	Long findLatestRateIdByCurrencyName(String name);
}
