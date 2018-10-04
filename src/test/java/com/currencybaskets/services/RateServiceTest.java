package com.currencybaskets.services;

import static com.currencybaskets.services.AccountServiceTest.LATEST_DATE;
import static com.currencybaskets.services.AccountServiceTest.NOT_LATEST_DATE;
import static com.currencybaskets.services.AccountServiceTest.NOT_NOT_LATEST_DATE;
import static com.currencybaskets.services.AccountServiceTest.assertHistory;
import static org.mockito.Mockito.when;

import com.currencybaskets.dao.model.Currency;
import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.HistoryDto;
import com.currencybaskets.dto.RateHistoryDto;
import com.currencybaskets.util.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
public class RateServiceTest {

  @TestConfiguration
  static class RateServiceTestConfiguration {
    @Bean
    RateService rateService() {
      return new RateService();
    }
  }

  @Autowired
  private RateService service;

  @MockBean
  private RateRepository rateRepository;

  @Test
  public void getHistory() throws Exception {
    when(rateRepository.findRatesOnDate(NOT_NOT_LATEST_DATE)).thenReturn(Arrays.asList(
        rate(Constants.USD, 10, NOT_NOT_LATEST_DATE),
        rate(Constants.EUR, 20, NOT_NOT_LATEST_DATE)
    ));
    when(rateRepository.findRatesAfterDate(NOT_NOT_LATEST_DATE)).thenReturn(Arrays.asList(
        rate(Constants.USD, 50, LATEST_DATE),
        rate(Constants.USD, 30, NOT_LATEST_DATE),
        rate(Constants.EUR, 40, NOT_LATEST_DATE)
    ));
    List<RateHistoryDto> actual = service.getHistory(NOT_NOT_LATEST_DATE);
    List<HistoryDto> eurHist = getByCurrency(actual, Constants.EUR);

    Assert.assertTrue(Objects.nonNull(eurHist));
    assertHistory(eurHist.get(0), 20, NOT_NOT_LATEST_DATE);
    assertHistory(eurHist.get(1), 40, NOT_LATEST_DATE);

    List<HistoryDto> usdHist = getByCurrency(actual, Constants.USD);
    Assert.assertTrue(Objects.nonNull(usdHist));
    assertHistory(usdHist.get(0), 10, NOT_NOT_LATEST_DATE);
    assertHistory(usdHist.get(1), 30, NOT_LATEST_DATE);
    assertHistory(usdHist.get(2), 50, LATEST_DATE);
  }

  private static List<HistoryDto> getByCurrency(List<RateHistoryDto> actual, String cur) {
    return actual.stream()
        .filter(h -> h.getCurrency().equals(cur))
        .findFirst()
        .map(RateHistoryDto::getHistory)
        .orElse(null);
  }

  private static Rate rate(String currency, double val, Date updated) {
    Rate rate = new Rate();
    rate.setUpdated(updated);
    rate.setRate(new BigDecimal(val));
    Currency cur = new Currency();
    cur.setName(currency);
    rate.setCurrency(cur);
    return rate;
  }



}
