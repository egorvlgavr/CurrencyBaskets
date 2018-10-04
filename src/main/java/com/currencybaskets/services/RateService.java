package com.currencybaskets.services;

import com.currencybaskets.dao.model.Rate;
import com.currencybaskets.dao.repository.RateRepository;
import com.currencybaskets.dto.HistoryDto;
import com.currencybaskets.dto.RateHistoryDto;
import com.currencybaskets.util.CurrencyColorMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RateService {

  @Autowired
  private RateRepository rateRepository;

  @Transactional
  public List<RateHistoryDto> getHistory(Date from) {
    List<Rate> ratesPrevious = rateRepository.findRatesOnDate(from);
    Map<String, TreeMap<Date, BigDecimal>> rateHistory = new HashMap<>();
    ratesPrevious.forEach(rate -> addToHistory(rateHistory, rate, from));
    List<Rate> rates = rateRepository.findRatesAfterDate(from);
    rates.forEach(rate -> addToHistory(rateHistory, rate));
    return toHistoryDto(rateHistory);
  }

  public List<RateHistoryDto> getHistory() {
    Iterable<Rate> rates = rateRepository.findAll();
    Map<String, TreeMap<Date, BigDecimal>> rateHistory = new HashMap<>();
    rates.forEach(rate -> addToHistory(rateHistory, rate));
    return toHistoryDto(rateHistory);
  }

  private static void addToHistory(Map<String, TreeMap<Date, BigDecimal>> ratesHistory,
                                   Rate rate) {
    addToHistory(ratesHistory, rate, rate.getUpdated());
  }

  private static void addToHistory(Map<String, TreeMap<Date, BigDecimal>> ratesHistory,
                                   Rate rate, Date updated) {
    TreeMap<Date, BigDecimal> history = ratesHistory.computeIfAbsent(rate.getCurrency().getName(),
        k -> new TreeMap<>());
    history.put(updated, rate.getRate());
  }

  private static List<RateHistoryDto> toHistoryDto(Map<String, TreeMap<Date, BigDecimal>> rateHistory) {
    List<RateHistoryDto> result = new ArrayList<>(rateHistory.size());
    for (Map.Entry<String, TreeMap<Date, BigDecimal>> currencyRates : rateHistory.entrySet()) {
      String key = currencyRates.getKey();
      RateHistoryDto dto = new RateHistoryDto(key, CurrencyColorMap.getCurrencyColor(key),
          currencyRates.getValue().entrySet()
              .stream()
              .map(entry -> new HistoryDto(entry.getKey(), entry.getValue()))
              .collect(Collectors.toList())
      );
      result.add(dto);
    }
    return result;
  }
}
