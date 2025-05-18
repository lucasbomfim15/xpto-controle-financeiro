package com.xpto.controlefinanceiro.modules.account.service;

import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;
import com.xpto.controlefinanceiro.modules.account.exceptions.AccountNotFoundException;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountsServiceImplTest {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private AccountsServiceImpl accountsService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        customerRepository = mock(CustomerRepository.class);
        accountsService = new AccountsServiceImpl(accountRepository, customerRepository);
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        AccountRequestDTO request = new AccountRequestDTO("BankX", "001", "12345", BigDecimal.TEN, customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
            Account acc = i.getArgument(0);
            acc.setId(UUID.randomUUID());
            return acc;
        });

        var response = accountsService.create(request);

        assertEquals("BankX", response.bank());
        assertEquals("001", response.agency());
        assertEquals("12345", response.number());
        assertEquals(BigDecimal.TEN, response.balance());
        assertEquals(customerId, response.customerId());
        assertTrue(response.active());
    }

    @Test
    void shouldThrowWhenCustomerNotFoundOnCreate() {
        UUID customerId = UUID.randomUUID();
        AccountRequestDTO request = new AccountRequestDTO("BankX", "001", "12345", BigDecimal.TEN, customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> accountsService.create(request));
    }

    @Test
    void shouldReturnAllAccounts() {
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        List<Account> accounts = List.of(
                Account.builder().id(UUID.randomUUID()).bank("Bank A").agency("001").number("111").balance(BigDecimal.TEN).initialBalance(BigDecimal.TEN).customer(customer).active(true).build()
        );

        when(accountRepository.findAll()).thenReturn(accounts);

        var result = accountsService.findAll();

        assertEquals(1, result.size());
        assertEquals("Bank A", result.get(0).bank());
    }

    @Test
    void shouldReturnAccountsByCustomerId() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        List<Account> accounts = List.of(
                Account.builder().id(UUID.randomUUID()).bank("Bank B").agency("002").number("222").balance(BigDecimal.ONE).initialBalance(BigDecimal.ONE).customer(customer).active(true).build()
        );

        when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);

        var result = accountsService.findByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals("Bank B", result.get(0).bank());
    }

    @Test
    void shouldFindAccountById() {
        UUID accountId = UUID.randomUUID();
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        Account account = Account.builder().id(accountId).bank("Bank C").agency("003").number("333").balance(BigDecimal.ONE).initialBalance(BigDecimal.ONE).customer(customer).active(true).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        var result = accountsService.findById(accountId);

        assertEquals("Bank C", result.bank());
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnFindById() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountsService.findById(accountId));
    }

    @Test
    void shouldUpdateAccount() {
        UUID accountId = UUID.randomUUID();
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        Account account = Account.builder().id(accountId).bank("Old Bank").agency("Old Agency").number("Old Number").balance(BigDecimal.ZERO).customer(customer).active(true).build();

        AccountUpdateDTO updateDTO = new AccountUpdateDTO("New Bank", "New Agency", "New Number", BigDecimal.TEN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        var updated = accountsService.update(accountId, updateDTO);

        assertEquals("New Bank", updated.bank());
        assertEquals("New Agency", updated.agency());
        assertEquals("New Number", updated.number());
        assertEquals(BigDecimal.TEN, updated.balance());
    }

    @Test
    void shouldDeleteAccountLogically() {
        UUID accountId = UUID.randomUUID();
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        Account account = Account.builder().id(accountId).bank("Bank").agency("Agency").number("Number").balance(BigDecimal.TEN).customer(customer).active(true).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        accountsService.delete(accountId);

        assertFalse(account.isActive());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnDelete() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountsService.delete(accountId));
    }
}
