package com.xpto.controlefinanceiro.modules.transaction.service;

import com.xpto.controlefinanceiro.modules.account.exceptions.AccountNotFoundException;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.exceptions.InsufficientBalanceException;
import com.xpto.controlefinanceiro.modules.transaction.mappers.TransactionMapper;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO dto) {
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + dto.accountId()));

        Transaction transaction = TransactionMapper.toEntity(dto, account);

        if (dto.type() == TransactionType.CREDIT) {
            account.setBalance(account.getBalance().add(dto.amount()));
        } else {
            if (account.getBalance().compareTo(dto.amount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance to perform this debit transaction");
            }
            account.setBalance(account.getBalance().subtract(dto.amount()));
        }

        accountRepository.save(account);
        Transaction saved = transactionRepository.save(transaction);

        return TransactionMapper.toResponseDTO(saved);
    }

    @Override
    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(TransactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> findByAccount(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }

        return transactionRepository.findByAccountId(accountId).stream()
                .map(TransactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> findByCustomer(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        return transactionRepository.findByAccountCustomerId(customerId).stream()
                .map(TransactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


}
