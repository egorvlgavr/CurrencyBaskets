package com.currencybaskets.tasks;

import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.fixer.CurrencyRate;
import com.currencybaskets.dto.fixer.LatestRate;
import com.currencybaskets.exceptions.ServiceUnavailableException;
import com.currencybaskets.services.AccountService;
import com.currencybaskets.services.CurrencyFixerClient;
import com.currencybaskets.util.Constants;
import com.currencybaskets.view.RateUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class UpdateLatestRateScheduledTask {

	@Autowired
	private CurrencyFixerClient fixer;

	@Autowired
	private AccountService accountService;

	@Autowired
	private RateRepository rateRepository;

	//Once per day
	@Scheduled(fixedRate = 24 * 60 * 60 * 60 * 1000L)
	public void updateLatestRate() {
		CurrencyRate currencyRate = fixer.getLatestRate();
		if (!currencyRate.isSuccess()) {
			throw new ServiceUnavailableException("Fixer service is not available");
		}
		LatestRate rates = currencyRate.getLatestRate();
		Long previousRateEurId = rateRepository.findLatestRateIdByCurrencyName(Constants.EUR);
		log.info("EUR rate with id={} will be updated", previousRateEurId);
		Long previousRateUsdId = rateRepository.findLatestRateIdByCurrencyName(Constants.USD);
		log.info("USD rate with id={} will be updated", previousRateUsdId);
		List<RateUpdate> updates = Arrays.asList(
				new RateUpdate(previousRateEurId, rates.getEurInRub()),
				new RateUpdate(previousRateUsdId, rates.getUsdInRub()));
		updates.forEach(update -> accountService.updateAccountsRate(update));
	}
}
