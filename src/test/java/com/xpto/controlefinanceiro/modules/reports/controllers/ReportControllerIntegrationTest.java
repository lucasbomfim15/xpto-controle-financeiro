package com.xpto.controlefinanceiro.modules.reports.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private Account account;
    private Transaction transactionCredit;
    private Transaction transactionDebit;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();

        // Criar cliente
        customer = new Customer();
        customer.setName("Cliente Teste");
        customer.setPhone("81983670589");
        customer.setCustomerType(CustomerType.PF);
        customer.setCreatedAt(LocalDate.now().minusYears(1));
        customerRepository.save(customer);

        // Criar conta para o cliente
        account = new Account();
        account.setCustomer(customer);
        account.setBank("Nubank Bank");
        account.setAgency("1234");
        account.setNumber("56789-0");
        account.setInitialBalance(BigDecimal.valueOf(1000));
        account.setBalance(BigDecimal.valueOf(1200));
        accountRepository.save(account);

        // Criar transação de crédito
        transactionCredit = new Transaction();
        transactionCredit.setAccount(account);
        transactionCredit.setAmount(BigDecimal.valueOf(300));
        transactionCredit.setType(TransactionType.CREDIT);
        transactionCredit.setDate(LocalDateTime.now().minusDays(10));
        transactionRepository.save(transactionCredit);

        // Criar transação de débito
        transactionDebit = new Transaction();
        transactionDebit.setAccount(account);
        transactionDebit.setAmount(BigDecimal.valueOf(100));
        transactionDebit.setType(TransactionType.DEBIT);
        transactionDebit.setDate(LocalDateTime.now().minusDays(5));
        transactionRepository.save(transactionDebit);
    }

    @Test
    public void testGetCustomerBalance() throws Exception {
        mockMvc.perform(get("/api/v1/reports/customer/{customerId}/balance", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Cliente Teste"))
                .andExpect(jsonPath("$.creditMovements").value(1))
                .andExpect(jsonPath("$.debitMovements").value(1))
                .andExpect(jsonPath("$.initialBalance").value(1000))
                .andExpect(jsonPath("$.currentBalance").value(1200));
    }

    @Test
    public void testGetCustomerBalancePeriod() throws Exception {
        String start = LocalDateTime.now().minusDays(15).toLocalDate().toString();
        String end = LocalDateTime.now().toLocalDate().toString();

        mockMvc.perform(get("/api/v1/reports/customer/{customerId}/balance-period", customer.getId())
                        .param("start", start)
                        .param("end", end)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Cliente Teste"))
                .andExpect(jsonPath("$.creditMovements").isNumber())
                .andExpect(jsonPath("$.debitMovements").isNumber());
    }

    @Test
    public void testGetBalanceSummary() throws Exception {
        mockMvc.perform(get("/api/v1/reports/customers/balance-summary")
                        .param("date", LocalDateTime.now().toLocalDate().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportDate").exists())
                .andExpect(jsonPath("$.customers").isArray());
    }

    @Test
    public void testGetCompanyRevenueReport() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        String startDate = LocalDate.now().minusMonths(1).format(formatter); // ex: 17/04/2025
        String endDate = LocalDate.now().format(formatter); // ex: 17/05/2025

        mockMvc.perform(get("/api/v1/reports/revenue")
                        .param("startDate", LocalDate.now().minusMonths(1).toString()) // ISO no parâmetro
                        .param("endDate", LocalDate.now().toString()) // ISO no parâmetro
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(startDate))
                .andExpect(jsonPath("$.endDate").value(endDate))
                .andExpect(jsonPath("$.totalRevenue").isNumber());
    }
}
