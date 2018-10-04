package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Rate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface RateRepository extends CrudRepository<Rate, Long> {
	@Query("SELECT MAX(r.id) FROM Rate r WHERE r.currency.id IN "
		+ "(SELECT r.currency.id FROM Rate r "
		+ "WHERE UPPER(r.currency.name) = UPPER(?1))")
	Long findLatestRateIdByCurrencyName(String name);

	@Query("SELECT r FROM Rate r WHERE r.id IN "
            + "(SELECT MAX(r.id) from Rate r "
            + "WHERE r.updated <= ?1 "
            + "GROUP BY r.currency.id)")
	List<Rate> findRatesOnDate(Date date);

	@Query("SELECT r FROM Rate r WHERE r.updated > ?1 ")
	List<Rate> findRatesAfterDate(Date date);
}
