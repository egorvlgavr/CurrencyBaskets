package com.currencybaskets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountUpdate {
    private long id;
    private int amount;
}
