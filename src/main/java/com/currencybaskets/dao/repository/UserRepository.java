package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
