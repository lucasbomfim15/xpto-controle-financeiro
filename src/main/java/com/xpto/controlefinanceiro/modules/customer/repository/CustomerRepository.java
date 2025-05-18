package com.xpto.controlefinanceiro.modules.customer.repository;

import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByCnpj(String cnpj);

    @Query(value = "SELECT fn_calcula_saldo_cliente(:customerId)", nativeQuery = true)
    BigDecimal calcularSaldoCliente(@Param("customerId") UUID customerId);
}
