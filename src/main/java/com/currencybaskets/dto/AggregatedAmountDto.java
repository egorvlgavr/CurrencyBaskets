package com.currencybaskets.dto;

import com.currencybaskets.dao.model.AggregatedAmount;
import com.currencybaskets.util.CurrencyColorMap;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggregatedAmountDto {
    private String currency;
    private BigDecimal amount;
    private String color;

    public static AggregatedAmountDto fromEntity(AggregatedAmount entity) {
        AggregatedAmountDto dto = new AggregatedAmountDto();
        dto.setAmount(entity.getAmount());
        String currency = entity.getCurrency().getName();
        dto.setCurrency(currency);
        dto.setColor(CurrencyColorMap.getCurrencyColor(currency));
        return dto;
    }
}
