package com.xpto.controlefinanceiro.modules.account.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;

import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID customerId;

    @BeforeEach
    public void setup() throws Exception {
        transactionRepository.deleteAll(); // deletar transações primeiro
        accountRepository.deleteAll();
        customerRepository.deleteAll();

        CustomerRequestDto customerDto = new CustomerRequestDto(
                "João da Silva",
                CustomerType.PF,
                "12345678901",
                null,
                "81999999999"
        );

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        customerId = UUID.fromString(mapper.readTree(response).get("id").asText());

    }

    @Test
    public void shouldCreateAccount() throws Exception {
        AccountRequestDTO dto = new AccountRequestDTO(
                "Banco XPTO", "1234", "56789-0", new BigDecimal("1500.00"), customerId
        );

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bank", is("Banco XPTO")))
                .andExpect(jsonPath("$.agency", is("1234")))
                .andExpect(jsonPath("$.number", is("56789-0")))
                .andExpect(jsonPath("$.balance", is(1500.00)))
                .andExpect(jsonPath("$.initialBalance", is(1500.00)))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())));
    }

    @Test
    public void shouldGetAllAccounts() throws Exception {
        shouldCreateAccount(); // Cria uma conta

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    public void shouldGetAccountById() throws Exception {
        AccountRequestDTO dto = new AccountRequestDTO(
                "Banco XPTO", "1234", "56789-0", new BigDecimal("1500.00"), customerId
        );

        String response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(response).get("id").asText());


        mockMvc.perform(get("/api/v1/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    public void shouldUpdateAccount() throws Exception {
        AccountRequestDTO createDto = new AccountRequestDTO(
                "Banco A", "0001", "12345-6", new BigDecimal("1000.00"), customerId
        );

        String response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(response).get("id").asText());


        AccountUpdateDTO updateDto = new AccountUpdateDTO(
                "Banco B", "0002", "65432-1", new BigDecimal("2500.00")
        );

        mockMvc.perform(put("/api/v1/accounts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bank", is("Banco B")))
                .andExpect(jsonPath("$.agency", is("0002")))
                .andExpect(jsonPath("$.number", is("65432-1")))
                .andExpect(jsonPath("$.balance", is(2500.00)));
    }

    @Test
    public void shouldDeleteAccount() throws Exception {
        AccountRequestDTO dto = new AccountRequestDTO(
                "Banco C", "1111", "22222-2", new BigDecimal("500.00"), customerId
        );

        String response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(response).get("id").asText());


        mockMvc.perform(delete("/api/v1/accounts/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    public void shouldGetAccountsByCustomerId() throws Exception {
        AccountRequestDTO dto = new AccountRequestDTO(
                "Banco XPTO", "1234", "56789-0", new BigDecimal("100.00"), customerId
        );

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accounts/customer/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }
}
