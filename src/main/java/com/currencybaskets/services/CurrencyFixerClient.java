package com.currencybaskets.services;

import com.currencybaskets.dto.fixer.CurrencyRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CurrencyFixerClient {

	private static final String BASE_URL = "http://data.fixer.io/api/";

	private final RestTemplate restTemplate;

	@Value("${application.fixer-key}")
	private String accessKey;

	public CurrencyFixerClient(RestTemplateBuilder restTemplateBuilder) {
		restTemplate = restTemplateBuilder.build();
	}

	public CurrencyRate getLatestRate() {
		String latestRateUrl = BASE_URL + "latest";
		UriComponentsBuilder builder = UriComponentsBuilder
			.fromUriString(latestRateUrl)
			.queryParam("access_key", accessKey)
			.queryParam("symbols", "RUB,USD,EUR");
		return restTemplate.getForObject(builder.toUriString(), CurrencyRate.class);
	}

}
