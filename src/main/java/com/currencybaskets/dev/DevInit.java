package com.currencybaskets.dev;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.Currency;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.model.User;
import com.currencybaskets.dao.repository.AccountRepository;
import com.currencybaskets.dao.repository.CurrencyRepository;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dao.repository.UserRepository;
import com.currencybaskets.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * The main porpoise of this class it to init DB with stub data.
 */
@Component
@Profile("dev")
public class DevInit implements CommandLineRunner {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CurrencyRepository currencyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RateRepository rateRepository;


  @Override
  public void run(String... args) throws Exception {
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
    Currency usd = currency(Constants.USD);
    Currency eur = currency(Constants.EUR);
    Currency rub = currency(Constants.RUB);

    ZonedDateTime now_zdt = ZonedDateTime.now();
    Date now = Date.from(now_zdt.toInstant());
    Date sixMonthAgo = Date.from(now_zdt.minusMonths(6).toInstant());
    Date twentyFiveDaysAgo = Date.from(now_zdt.minusDays(25).toInstant());
    Date sixDaysAgo = Date.from(now_zdt.minusDays(6).toInstant());

    // Create rate
    Rate usd_rate1 = rate(usd, 50.0, sixMonthAgo);
    Rate eur_rate1 = rate(eur, 100, sixMonthAgo);


    // Use all in account
    String raiffeisen = "Raiffeisen";
    String alfa = "Alfa-Bank";

    // six month ago: only 2 accounts
    Account raif_usd1 = account(20.0, usd_rate1, raiffeisen, usr1, sixMonthAgo);
    Account raif_eur1 = account(30.0, eur_rate1, raiffeisen, usr2, sixMonthAgo);


    // 25 days ago: 1 rate update + 1 new account
    Account alfa_rub = account(1800.0, rub, alfa, usr1, twentyFiveDaysAgo);
    Rate usd_rate2 = updateRate(55.0, usd_rate1, twentyFiveDaysAgo);
    Account raif_usd2 = updateAccountOnRate(raif_usd1, usd_rate2, twentyFiveDaysAgo);

    // 6 days ago: 1 rate update + 1 ammount update
    Rate eur_rate2 = updateRate(200.0, eur_rate1, sixDaysAgo);
    Account raif_eur2 = updateAccountOnRate(raif_eur1, eur_rate2, sixDaysAgo);
    updateAccountAmount(alfa_rub, 1400.0, sixDaysAgo);

    // now: 2 ammount updates
    updateAccountAmount(raif_usd2, 25.0, now);
    updateAccountAmount(raif_eur2, 42.0, now);

  }

  private Currency currency(String name) {
    Currency currency = new Currency();
    currency.setName(name);
    currencyRepository.save(currency);
    return currency;
  }

  private Rate rate(Currency currency, double val, Date updated) {
    Rate rate = new Rate();
    rate.setCurrency(currency);
    rate.setVersion(0);
    rate.setRate(new BigDecimal(val));
    rate.setUpdated(updated);
    rateRepository.save(rate);
    return rate;
  }

  private Account account(double val, Rate rate, String bank, User usr, Date updated) {
    Account account = new Account();
    account.setBank(bank);
    account.setAmount(new BigDecimal(val));
    account.setAmountChange(new BigDecimal(val));
    account.setAmountBase(account.getAmount().multiply(rate.getRate()));
    account.setAmountBaseChange(account.getAmountBase());
    account.setUpdated(updated);
    account.setPreviousId(-1L);
    account.setVersion(1);
    account.setUser(usr);
    account.setCurrency(rate.getCurrency());
    account.setRate(rate);
    accountRepository.save(account);
    return account;
  }

  private Account account(double val, Currency currency, String bank, User usr, Date updated) {
    Account account = new Account();
    account.setBank(bank);
    BigDecimal amount = new BigDecimal(val);
    account.setAmount(amount);
    account.setAmountChange(amount);
    account.setAmountBase(amount);
    account.setAmountBaseChange(amount);
    account.setUpdated(updated);
    account.setPreviousId(-1L);
    account.setVersion(1);
    account.setUser(usr);
    account.setCurrency(currency);
    accountRepository.save(account);
    return account;
  }

  private Rate updateRate(double val, Rate previous, Date updated) {
    Rate update = previous.createRateUpdate(new BigDecimal(val));
    update.setUpdated(updated);
    rateRepository.save(update);
    return update;
  }

  private Account updateAccountOnRate(Account previous, Rate newRate, Date updated) {
    Account accountRateUpdate = previous.createAccountRateUpdate(newRate);
    accountRateUpdate.setUpdated(updated);
    accountRepository.save(accountRateUpdate);
    return accountRateUpdate;
  }

  private Account updateAccountAmount(Account previous, double val, Date updated) {
    Account accountUpdate = previous.createAccountAmountUpdate(new BigDecimal(val));
    accountUpdate.setUpdated(updated);
    accountRepository.save(accountUpdate);
    return accountUpdate;
  }
}
