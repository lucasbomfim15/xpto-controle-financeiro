package com.xpto.controlefinanceiro.modules.account.repository;

import com.xpto.controlefinanceiro.modules.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomerId(UUID customerId);

    int countByCustomerId(UUID customerId);

    void deleteAllByCustomerId(UUID customerId);

    List<Account> findAllByCustomerId(UUID customerId);
}
