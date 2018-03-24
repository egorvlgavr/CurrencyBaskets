package com.currencybaskets.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "rates")
@ToString(exclude = "accounts")
@EqualsAndHashCode(exclude = "accounts")
@NoArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "updated")
    @Temporal(TemporalType.DATE)
    private Date updated;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts;
}
