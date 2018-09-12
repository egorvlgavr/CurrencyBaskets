package com.currencybaskets.dto.fixer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestRate
{

	@JsonDeserialize(as = BigDecimal.class)
	private BigDecimal USD;

	@JsonDeserialize(as = BigDecimal.class)
	private BigDecimal RUB;

	@JsonDeserialize(as = BigDecimal.class)
	private BigDecimal EUR;

	public BigDecimal getEurInRub() {
		return this.getRUB();
	}

	public BigDecimal getUsdInRub() {
		return this.getRUB().divide(this.getUSD(), RoundingMode.HALF_UP);
	}
}
