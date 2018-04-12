package com.currencybaskets.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "currency")
@ToString(exclude = "accounts")
@EqualsAndHashCode(exclude = {"accounts", "id"})
@NoArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts;
}
