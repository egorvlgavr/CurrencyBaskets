package com.currencybaskets.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "updated"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "bank", nullable = false)
    private String bank;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "version", nullable = false)
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Column(name = "previous_id", nullable = false)
    private Long previousId;

    @ManyToOne
    @JoinColumn(name = "rate_id")
    private Rate rate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "amount_change")
    private BigDecimal amountChange;

    @Column(name = "amount_base", nullable = false)
    private BigDecimal amountBase;

    @Column(name = "amount_base_change")
    private BigDecimal amountBaseChange;

    @Column(name = "updated")
    @Temporal(TemporalType.DATE)
    private Date updated;

    public Account createAccountAmountUpdate(BigDecimal newAmount) {
        Account updated = new Account();
        updated.setBank(bank);
        updated.setCurrency(currency);
        updated.setUser(user);
        updated.setRate(rate);
        updated.setPreviousId(id);
        updated.setVersion(version + 1);
        updated.setAmount(newAmount);
        updated.setAmountChange(newAmount.subtract(amount));
        BigDecimal newAmountBase = Objects.nonNull(rate) ? newAmount.multiply(rate.getRate()) : newAmount;
        updated.setAmountBase(newAmountBase);
        updated.setAmountBaseChange(newAmountBase.subtract(amountBase));
        updated.setUpdated(Date.from(ZonedDateTime.now().toInstant()));
        return updated;
    }

    public Account createAccountRateUpdate(Rate newRate) {
        Account updated = new Account();
        updated.setBank(bank);
        updated.setCurrency(currency);
        updated.setUser(user);
        updated.setRate(newRate);
        updated.setPreviousId(id);
        updated.setVersion(version + 1);
        updated.setAmount(amount);
        updated.setAmountChange(BigDecimal.ZERO);
        BigDecimal newAmountBase = amount.multiply(newRate.getRate());
        updated.setAmountBase(newAmountBase);
        updated.setAmountBaseChange(newAmountBase.subtract(amountBase));
        updated.setUpdated(Date.from(ZonedDateTime.now().toInstant()));
        return updated;
    }
}
