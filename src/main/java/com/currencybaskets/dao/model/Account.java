package com.currencybaskets.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
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
}
