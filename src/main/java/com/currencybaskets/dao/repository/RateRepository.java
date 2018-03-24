package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.Rate;
import org.springframework.data.repository.CrudRepository;

public interface RateRepository extends CrudRepository<Rate, Long> {
}
