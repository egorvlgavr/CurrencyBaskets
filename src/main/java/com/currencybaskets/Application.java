package com.currencybaskets;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Currency;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.model.User;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dao.repository.CurrencyRepository;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dao.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner demo(AccountRepository accountRepository,
                                  CurrencyRepository currencyRepository,
                                  UserRepository userRepository,
                                  RateRepository rateRepository) {
        return (args -> {
            // Create user
            User usr1 = new User();
            usr1.setName("Ivan");
            usr1.setSurname("Ivanov");
            usr1.setGroupId(2L);
            usr1.setColor("#DEB887");
            userRepository.save(usr1);

            // Create currency
            Currency currency = new Currency();
            currency.setName("USD");
            currencyRepository.save(currency);

            // Create rate
            Rate rate = new Rate();
            rate.setCurrency(currency);
            rate.setVersion(0);
            rate.setRate(new BigDecimal(67.43));
            rate.setUpdated(new Date());
            rateRepository.save(rate);

            // Use all in account
            Account account = new Account();
            account.setAmount(new BigDecimal(10.5));
            account.setBank("Raiffeisen");
            account.setAmountBase(new BigDecimal(10.5));
            account.setUpdated(new Date());
            account.setPreviousId(-1L);
            account.setVersion(1);
            account.setUser(usr1);
            account.setCurrency(currency);
            account.setRate(rate);
            accountRepository.save(account);

            Account account1 = new Account();
            account1.setAmount(new BigDecimal(11.5));
            account1.setAmountChange(new BigDecimal(1.0));
            account1.setBank("Raiffeisen");
            account1.setAmountBase(new BigDecimal(11.5));
            account1.setAmountBaseChange(new BigDecimal(1.0));
            account1.setUpdated(new Date());
            account1.setPreviousId(1L);
            account1.setVersion(2);
            account1.setUser(usr1);
            account1.setCurrency(currency);
            account1.setRate(rate);
            accountRepository.save(account1);

            Account account2 = new Account();
            account2.setAmount(new BigDecimal(22.1));
            account2.setBank("Alfa-Bank");
            account2.setAmountBase(new BigDecimal(22.1));
            account2.setUpdated(new Date());
            account2.setPreviousId(1L);
            account2.setVersion(1);
            account2.setUser(usr1);
            account2.setCurrency(currency);
            account2.setRate(rate);
            accountRepository.save(account2);
        });
    }
}
