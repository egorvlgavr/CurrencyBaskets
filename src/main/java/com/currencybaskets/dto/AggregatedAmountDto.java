package com.currencybaskets.dto;

import com.currencybaskets.dao.model.AggregatedAmount;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggregatedAmountDto {
    private String currency;
    private BigDecimal amount;

    public static AggregatedAmountDto fromEntity(AggregatedAmount entity) {
        AggregatedAmountDto dto = new AggregatedAmountDto();
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency().getName());
        return dto;
    }
}
