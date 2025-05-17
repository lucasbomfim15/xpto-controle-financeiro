package com.xpto.controlefinanceiro.modules.account.model;

import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, columnDefinition = "NUMERIC(19,2) DEFAULT 0.0")
    private BigDecimal initialBalance = BigDecimal.ZERO;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private boolean active = true;
}
