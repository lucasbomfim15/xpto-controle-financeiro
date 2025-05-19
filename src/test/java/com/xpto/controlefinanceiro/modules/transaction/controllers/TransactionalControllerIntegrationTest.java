package com.xpto.controlefinanceiro.modules.transaction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    private UUID customerId;
    private UUID accountId;

    @BeforeEach
    void setup() throws Exception {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        // Criar Customer
        CustomerRequestDto customer = new CustomerRequestDto(
                "Lucas Teste",
                CustomerType.PF,
                "12345678901",
                null,
                "81999998888"
        );

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andReturn();

        String customerResponse = customerResult.getResponse().getContentAsString();
        customerId = UUID.fromString(objectMapper.readTree(customerResponse).get("id").asText());

        // Criar Account
        AccountRequestDTO account = new AccountRequestDTO(
                "XPTO Bank",
                "0001",
                "123456-7",
                BigDecimal.valueOf(1000),
                customerId
        );

        MvcResult accountResult = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andReturn();

        String accountResponse = accountResult.getResponse().getContentAsString();
        accountId = UUID.fromString(objectMapper.readTree(accountResponse).get("id").asText());
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                accountId,
                TransactionType.CREDIT,
                BigDecimal.valueOf(500),
                "Salário"
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.type").value("CREDIT"))
                .andExpect(jsonPath("$.description").value("Salário"));
    }

    @Test
    void shouldFindAllTransactions() throws Exception {
        // Criar transação para garantir que existirá pelo menos uma
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                accountId,
                TransactionType.CREDIT,
                BigDecimal.valueOf(300),
                "Bônus"
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }


    @Test
    void shouldFindTransactionsByAccountId() throws Exception {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                accountId,
                TransactionType.DEBIT,
                BigDecimal.valueOf(200),
                "Compra"
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/transactions/account/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFindTransactionsByCustomerId() throws Exception {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                accountId,
                TransactionType.CREDIT,
                BigDecimal.valueOf(700),
                "Depósito"
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/transactions/customer/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldFailWhenInsufficientBalanceForDebit() throws Exception {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                accountId,
                TransactionType.DEBIT,
                BigDecimal.valueOf(999999),
                "Compra grande"
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance to perform this debit transaction"));
    }
}
