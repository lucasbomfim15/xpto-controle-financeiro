package com.xpto.controlefinanceiro.modules.reports.services;

import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.reports.dtos.*;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Customer mockCustomer;
    private Account mockAccount;
    private List<Transaction> mockTransactions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCustomer = new Customer();
        mockCustomer.setId(UUID.randomUUID());
        mockCustomer.setName("Test Customer");
        mockCustomer.setCreatedAt(LocalDate.now());
        mockCustomer.setAddresses(List.of());



        mockAccount = new Account();
        mockAccount.setId(UUID.randomUUID());
        mockAccount.setCustomer(mockCustomer);
        mockAccount.setInitialBalance(new BigDecimal("100.00"));
        mockAccount.setBalance(new BigDecimal("150.00"));

        Transaction tx1 = new Transaction();
        tx1.setId(UUID.randomUUID());
        tx1.setAccount(mockAccount);
        tx1.setType(TransactionType.CREDIT);
        tx1.setAmount(new BigDecimal("30.00"));
        tx1.setDate(LocalDateTime.now());

        Transaction tx2 = new Transaction();
        tx2.setId(UUID.randomUUID());
        tx2.setAccount(mockAccount);
        tx2.setType(TransactionType.DEBIT);
        tx2.setAmount(new BigDecimal("20.00"));
        tx2.setDate(LocalDateTime.now());

        mockTransactions = List.of(tx1, tx2);
    }

    @Test
    void testGenerateCustomerBalanceReport() {
        UUID customerId = mockCustomer.getId();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccountId(mockAccount.getId())).thenReturn(mockTransactions);

        CustomerBalanceReportDTO report = reportService.generateCustomerBalanceReport(customerId);

        assertNotNull(report);
        assertEquals("Test Customer", report.customerName());
        assertEquals(new BigDecimal("100.00"), report.initialBalance());
        assertEquals(new BigDecimal("150.00"), report.currentBalance());
        assertEquals(1, report.creditMovements());
        assertEquals(1, report.debitMovements());
        assertEquals(2, report.totalMovements());
    }

    @Test
    void testGenerateCustomerBalancePeriodReport() {
        UUID customerId = mockCustomer.getId();
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccountId(mockAccount.getId())).thenReturn(mockTransactions);

        CustomerBalancePeriodReportDTO report = reportService.generateCustomerBalancePeriodReport(customerId, start, end);

        assertNotNull(report);
        assertEquals("Test Customer", report.customerName());
        assertEquals(1, report.creditMovements());
        assertEquals(1, report.debitMovements());
        assertEquals(2, report.totalMovements());
    }

    @Test
    void testGenerateSummaryReport() {
        LocalDate date = LocalDate.now();
        when(customerRepository.findAll()).thenReturn(List.of(mockCustomer));
        when(accountRepository.findByCustomerId(mockCustomer.getId())).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccountIdAndDateLessThanEqual(eq(mockAccount.getId()), any()))
                .thenReturn(mockTransactions);

        CustomersBalanceSummaryReportDTO report = reportService.generateSummaryReport(date);

        assertNotNull(report);
        assertEquals(1, report.customers().size());
        assertEquals("Test Customer", report.customers().get(0).customerName());
    }

    @Test
    void testGenerateCompanyRevenueReport() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now().plusDays(5);

        when(customerRepository.findAll()).thenReturn(List.of(mockCustomer));
        when(accountRepository.findByCustomerId(mockCustomer.getId())).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccountId(mockAccount.getId())).thenReturn(mockTransactions);

        CompanyRevenueReportDTO report = reportService.generateCompanyRevenueReport(start, end);

        assertNotNull(report);
        assertEquals(1, report.customers().size());
        assertEquals("Test Customer", report.customers().get(0).customerName());
    }
}