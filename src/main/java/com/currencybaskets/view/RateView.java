package com.currencybaskets.view;

import com.currencybaskets.dao.model.Rate;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateView {
    private Long id;
    private String currency;
    private BigDecimal rate;

    public static RateView fromEntity(Rate entity) {
        RateView rateView = new RateView();
        rateView.setId(entity.getId());
        rateView.setCurrency(entity.getCurrency().getName());
        rateView.setRate(entity.getRate());
        return rateView;
    }
}
