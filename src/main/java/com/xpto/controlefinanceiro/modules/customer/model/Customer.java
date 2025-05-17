package com.xpto.controlefinanceiro.modules.customer.model;

import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String phone;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;


    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Address> addresses;
}
