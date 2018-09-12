package com.currencybaskets.tasks;

import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.fixer.CurrencyRate;
import com.currencybaskets.dto.fixer.LatestRate;
import com.currencybaskets.exceptions.ServiceUnavailableException;
import com.currencybaskets.services.AccountService;
import com.currencybaskets.services.CurrencyFixerClient;
import com.currencybaskets.view.RateUpdate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UpdateLatestRateScheduledTaskTest {

	@MockBean
	private CurrencyFixerClient fixerClient;

	@MockBean
	private AccountService accountService;

	@MockBean
	private RateRepository rateRepository;

	@TestConfiguration
	static class UpdateLatestRateScheduledTaskTestConfiguration {
		@Bean
		UpdateLatestRateScheduledTask updateLatestRateScheduledTask() {
			return new UpdateLatestRateScheduledTask();
		}
	}

	@Autowired
	private UpdateLatestRateScheduledTask task;

	@Test
	public void updateLatestRateOk() throws Exception {
		LatestRate rate =  new LatestRate();
		rate.setEUR(new BigDecimal(78.898669));
		rate.setRUB(new BigDecimal(1.155822));
		rate.setUSD(new BigDecimal(1));
		CurrencyRate rateCR = new CurrencyRate();
		rateCR.setBase("EUR");
		rateCR.setDate("2018-09-04");
		rateCR.setSuccess(true);
		rateCR.setTimestamp(1536057556L);
		rateCR.setLatestRate(rate);
		when(fixerClient.getLatestRate()).thenReturn(rateCR);
		when(rateRepository.findLatestRateIdByCurrencyName("EUR")).thenReturn(1L);
		when(rateRepository.findLatestRateIdByCurrencyName("USD")).thenReturn(2L);
		task.updateLatestRate();
		verify(accountService, times(1)).updateAccountsRate(new RateUpdate(1L, rate.getEurInRub()));
		verify(accountService, times(1)).updateAccountsRate(new RateUpdate(2L, rate.getEurInRub()));
	}

	@Test(expected = ServiceUnavailableException.class)
	public void updateLatestRateFixerUnavailable() {
		CurrencyRate rateCR = new CurrencyRate();
		rateCR.setSuccess(false);
		when(fixerClient.getLatestRate()).thenReturn(rateCR);
		task.updateLatestRate();
	}
}
