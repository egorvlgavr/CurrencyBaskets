package com.currencybaskets.view;

import com.currencybaskets.dao.model.Account;
import com.currencybaskets.dao.model.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountView {
    private Long id;
    private String bank;
    private String currency;
    private String userFullName;
    private BigDecimal amount;
    private BigDecimal amountBase;
    private Date updated;
    // TODO add limit warning per bank

    public static AccountView fromEntity(Account entity) {
        AccountView view = new AccountView();
        view.setId(entity.getId());
        view.setBank(entity.getBank());
        view.setCurrency(entity.getCurrency().getName());
        view.setAmount(entity.getAmount());
        view.setAmountBase(entity.getAmountBase());
        view.setUpdated(entity.getUpdated());
        User user = entity.getUser();
        view.setUserFullName(user.getName() + " " + user.getSurname());
        return view;
    }
}
