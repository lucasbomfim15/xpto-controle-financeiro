package com.xpto.controlefinanceiro.modules.transaction.services;

import com.xpto.controlefinanceiro.modules.account.exceptions.AccountNotFoundException;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.exceptions.InsufficientBalanceException;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;

import com.xpto.controlefinanceiro.modules.transaction.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);

        transactionService = new TransactionServiceImpl(
                transactionRepository,
                accountRepository,
                customerRepository
        );
    }

    @Test
    void shouldCreateCreditTransactionAndUpdateAccountBalance() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        Account account = Account.builder()
                .id(accountId)
                .balance(new BigDecimal("200.00"))
                .build();

        TransactionRequestDTO dto = new TransactionRequestDTO(
                accountId,
                TransactionType.CREDIT,
                amount,
                "Salary"
        );

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .account(account)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .description("Salary")
                .date(LocalDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.create(dto);

        assertNotNull(response);
        assertEquals(accountId, response.accountId());
        assertEquals(TransactionType.CREDIT, response.type());
        assertEquals(amount, response.amount());
        assertEquals("Salary", response.description());

        assertEquals(new BigDecimal("300.00"), account.getBalance());
    }

    @Test
    void shouldCreateDebitTransactionAndUpdateAccountBalance() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        Account account = Account.builder()
                .id(accountId)
                .balance(new BigDecimal("200.00"))
                .build();

        TransactionRequestDTO dto = new TransactionRequestDTO(
                accountId,
                TransactionType.DEBIT,
                amount,
                "Groceries"
        );

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .account(account)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .description("Groceries")
                .date(LocalDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.create(dto);

        assertNotNull(response);
        assertEquals(accountId, response.accountId());
        assertEquals(TransactionType.DEBIT, response.type());
        assertEquals(amount, response.amount());
        assertEquals("Groceries", response.description());

        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenDebitingMoreThanAvailable() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("300.00");

        Account account = Account.builder()
                .id(accountId)
                .balance(new BigDecimal("200.00"))
                .build();

        TransactionRequestDTO dto = new TransactionRequestDTO(
                accountId,
                TransactionType.DEBIT,
                amount,
                "Big Purchase"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.create(dto);
        });

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccountNotFoundException() {
        UUID accountId = UUID.randomUUID();

        TransactionRequestDTO dto = new TransactionRequestDTO(
                accountId,
                TransactionType.CREDIT,
                new BigDecimal("100.00"),
                "Some desc"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.create(dto);
        });

        verify(transactionRepository, never()).save(any());
    }


    @Test
    void shouldReturnAllTransactions() {
        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .account(Account.builder().id(UUID.randomUUID()).build())
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.CREDIT)
                .description("T1")
                .date(LocalDateTime.now())
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .account(Account.builder().id(UUID.randomUUID()).build())
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.DEBIT)
                .description("T2")
                .date(LocalDateTime.now())
                .build();

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        var result = transactionService.findAll();

        assertEquals(2, result.size());
        assertEquals(transaction1.getId(), result.get(0).id());
        assertEquals(transaction2.getId(), result.get(1).id());
    }


    @Test
    void shouldReturnTransactionsByAccountId() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.existsById(accountId)).thenReturn(true);

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .account(Account.builder().id(accountId).build())
                .amount(new BigDecimal("150.00"))
                .type(TransactionType.DEBIT)
                .description("By Account")
                .date(LocalDateTime.now())
                .build();

        when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of(transaction));

        var result = transactionService.findByAccount(accountId);

        assertEquals(1, result.size());
        assertEquals(accountId, result.get(0).accountId());
    }

    @Test
    void shouldThrowAccountNotFoundWhenFindingByAccountId() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.findByAccount(accountId);
        });

        verify(transactionRepository, never()).findByAccountId(accountId);
    }

    @Test
    void shouldReturnTransactionsByCustomerId() {
        UUID customerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        when(customerRepository.existsById(customerId)).thenReturn(true);

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .account(Account.builder().id(accountId).build())
                .amount(new BigDecimal("250.00"))
                .type(TransactionType.CREDIT)
                .description("By Customer")
                .date(LocalDateTime.now())
                .build();

        when(transactionRepository.findByAccountCustomerId(customerId)).thenReturn(List.of(transaction));

        var result = transactionService.findByCustomer(customerId);

        assertEquals(1, result.size());
        assertEquals(transaction.getId(), result.get(0).id());
        assertEquals(accountId, result.get(0).accountId());
    }

    @Test
    void shouldThrowCustomerNotFoundWhenFindingByCustomerId() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> {
            transactionService.findByCustomer(customerId);
        });

        verify(transactionRepository, never()).findByAccountCustomerId(customerId);
    }


}
