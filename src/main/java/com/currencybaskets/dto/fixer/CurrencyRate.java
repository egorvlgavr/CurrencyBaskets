package com.currencybaskets.dto.fixer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {

	private boolean success;

	private Long timestamp;

	private String base;

	private String date;

	@JsonProperty("rates")
	private LatestRate latestRate;

}
