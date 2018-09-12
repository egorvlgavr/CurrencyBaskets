package com.currencybaskets.services;

import com.currencybaskets.dto.fixer.CurrencyRate;
import com.currencybaskets.dto.fixer.LatestRate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(CurrencyFixerClient.class)
public class CurrencyFixerClientTest {

	@Autowired
	private CurrencyFixerClient client;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${application.fixer-key}")
	private String accessKey;

	@Test
	public void whenCallingGetLatestRate_thenClientMakesCorrectCall() throws JsonProcessingException
	{
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
		String response = objectMapper.writeValueAsString(rateCR);
		this.server.expect(requestTo("http://data.fixer.io/api/latest?access_key=" +
				accessKey + "&symbols=RUB,USD,EUR"))
				.andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
		CurrencyRate rateActual = this.client.getLatestRate();
		assertThat(rateActual.getBase()).isEqualTo("EUR");
		assertThat(rateActual.getDate()).isEqualTo("2018-09-04");
		assertThat(rateActual.isSuccess()).isTrue();
		assertThat(rateActual.getTimestamp()).isEqualTo(1536057556L);
		assertThat(rateActual.getLatestRate().getUSD()).isEqualTo(new BigDecimal(1));
		assertThat(rateActual.getLatestRate().getRUB()).isEqualTo(new BigDecimal(1.155822));
		assertThat(rateActual.getLatestRate().getEUR()).isEqualTo(new BigDecimal(78.898669));
	}

	@Test
	public void whenCallingGetLatestRate_thenClientReturnsErrorResponse() throws Exception
	{
		JSONObject resp = new JSONObject();
		JSONObject errorObj = new JSONObject();
		errorObj.put("code", 104);
		errorObj.put("info", "Our monthly API request volume has been reached. Please upgrade your plan.");
		resp.put("success", false);
		resp.put("error", errorObj);
		this.server.expect(requestTo("http://data.fixer.io/api/latest?access_key=" +
				accessKey + "&symbols=RUB,USD,EUR"))
			.andRespond(withSuccess(resp.toString(), MediaType.APPLICATION_JSON));
		CurrencyRate rateActual = this.client.getLatestRate();
		assertThat(rateActual.isSuccess()).isFalse();
		assertThat(rateActual.getBase()).isNull();
		assertThat(rateActual.getDate()).isNull();
		assertThat(rateActual.getLatestRate()).isNull();
		assertThat(rateActual.getTimestamp()).isNull();
	}
}
