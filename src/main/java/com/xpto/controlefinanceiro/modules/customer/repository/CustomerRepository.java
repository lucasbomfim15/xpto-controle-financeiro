package com.xpto.controlefinanceiro.modules.customer.repository;

import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByCnpj(String cnpj);
}
