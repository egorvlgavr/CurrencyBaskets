package com.currencybaskets.dao.repository;

import com.currencybaskets.dao.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT u.id FROM User u WHERE u.groupId = " +
            "(SELECT u.groupId FROM User u WHERE u.id = ?1)")
    List<Long> getUserIdsInSameGroup(Long userId);
}
