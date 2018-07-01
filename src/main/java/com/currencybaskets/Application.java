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
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("dev")
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

            User usr2 = new User();
            usr2.setName("Petr");
            usr2.setSurname("Petrov");
            usr2.setGroupId(2L);
            usr2.setColor("#DEB889");
            userRepository.save(usr2);

            // Create currency
            Currency currency = new Currency();
            currency.setName("USD");
            currencyRepository.save(currency);

            Currency currency1 = new Currency();
            currency1.setName("EUR");
            currencyRepository.save(currency1);

            Currency currency2 = new Currency();
            currency2.setName("RUB");
            currencyRepository.save(currency2);

            ZonedDateTime now = ZonedDateTime.now();
            Date monthAgo = Date.from(now.minusMonths(1).toInstant());
            Date weekAgo = Date.from(now.minusWeeks(1).toInstant());

            // Create rate
            Rate rate = new Rate();
            rate.setCurrency(currency);
            rate.setVersion(0);
            rate.setRate(new BigDecimal(67.43));
            rate.setUpdated(Date.from(now.toInstant()));
            rateRepository.save(rate);

            Rate rate1 = new Rate();
            rate1.setCurrency(currency1);
            rate1.setVersion(0);
            rate1.setRate(new BigDecimal(56.33));
            rate1.setUpdated(monthAgo);
            rateRepository.save(rate1);

            // Use all in account
            Account account = new Account();
            account.setAmount(new BigDecimal(1000.5));
            account.setBank("Raiffeisen");
            account.setAmountBase(new BigDecimal(1000.5));
            account.setUpdated(monthAgo);
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
            account1.setUpdated(weekAgo);
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

            Account account3 = new Account();
            account3.setAmount(new BigDecimal(75.1));
            account3.setBank("Alfa-Bank");
            account3.setAmountBase(new BigDecimal(75.1));
            account3.setUpdated(monthAgo);
            account3.setPreviousId(1L);
            account3.setVersion(1);
            account3.setUser(usr1);
            account3.setCurrency(currency1);
            account3.setRate(rate1);
            accountRepository.save(account3);

            Account account4 = new Account();
            account4.setAmount(new BigDecimal(142.1));
            account4.setBank("Alfa-Bank");
            account4.setAmountBase(new BigDecimal(887.1));
            account4.setUpdated(new Date());
            account4.setPreviousId(1L);
            account4.setVersion(1);
            account4.setUser(usr2);
            account4.setCurrency(currency1);
            account4.setRate(rate1);
            accountRepository.save(account4);

            Account account5 = new Account();
            account5.setAmount(new BigDecimal(1451.8));
            account5.setBank("Tinkoff");
            account5.setAmountBase(new BigDecimal(1451.8));
            account5.setUpdated(new Date());
            account5.setPreviousId(1L);
            account5.setVersion(1);
            account5.setUser(usr2);
            account5.setCurrency(currency2);
            accountRepository.save(account5);
        });
    }
}
