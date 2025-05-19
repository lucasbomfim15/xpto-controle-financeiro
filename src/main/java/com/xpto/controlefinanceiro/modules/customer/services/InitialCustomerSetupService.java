package com.xpto.controlefinanceiro.modules.customer.services;

import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InitialCustomerSetupService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public InitialCustomerSetupService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void setupInitialAccountAndTransaction(Customer customer) {
        // Cria uma conta bancária inicial
        Account account = new Account();
        account.setCustomer(customer);
        account.setBank("XPTO Bank");
        account.setAgency("0001");
        account.setNumber(UUID.randomUUID().toString().substring(0, 10));
        account.setBalance(BigDecimal.ZERO);

        accountRepository.save(account);

        // Cria uma transação inicial de crédito
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.CREDIT);
        transaction.setDescription("Initial deposit");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        // Atualiza o saldo da conta
        account.setBalance(transaction.getAmount());
        accountRepository.save(account);
    }
}
