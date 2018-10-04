package com.currencybaskets.util;


import java.util.HashMap;
import java.util.Map;

public final class CurrencyColorMap {
  private static final Map<String, String> currencyColors;

  static {
    // TODO store Currency colors in database
    currencyColors = new HashMap<>();
    currencyColors.put(Constants.USD, "#3cba9f");
    currencyColors.put(Constants.EUR, "#3e95cd");
    currencyColors.put(Constants.RUB, "#c45850");
  }

  public static String getCurrencyColor(String cur) {
    return currencyColors.getOrDefault(cur, "#8e5ea2");
  }
}
