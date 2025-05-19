package com.xpto.controlefinanceiro.modules.transaction.repository;

import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccountId(UUID accountId);
    List<Transaction> findByAccountCustomerId(UUID customerId);
    List<Transaction> findByAccountIdAndDateLessThanEqual(UUID accountId, LocalDateTime date);
    void deleteAllByAccountId(UUID id);

    boolean existsByAccountId(UUID id);
}
