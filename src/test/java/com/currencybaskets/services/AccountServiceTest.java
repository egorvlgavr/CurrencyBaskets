package com.currencybaskets.services;

import com.currencybaskets.dao.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @TestConfiguration
    static class AccountServiceTestConfiguration {
        @Bean
        AccountService accountService() {
            return new AccountService();
        }
    }

    @Autowired
    private AccountService service;

    @MockBean
    private AccountRepository repository;

    @Test
    public void getUserLatestAccounts() throws Exception {
        assertTrue(service != null);
        // TODD check null rate
        // TODD check total aggregation
    }

    @Test
    public void updateAccountAmount() throws Exception {
    }

}