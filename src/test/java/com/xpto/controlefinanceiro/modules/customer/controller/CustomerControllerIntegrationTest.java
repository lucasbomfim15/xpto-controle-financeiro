package com.xpto.controlefinanceiro.modules.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Lucas Nascimento",
                CustomerType.PF,
                "12345678900",
                null,
                "81999999999"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Lucas Nascimento"))
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.customerType").value("PF"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnAllCustomers() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "João Silva",
                CustomerType.PF,
                "11122233344",
                null,
                "81988887777"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("João Silva")));

    }

    @Test
    void shouldReturnCustomerById() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Ana Paula",
                CustomerType.PF,
                "99988877766",
                null,
                "81988886666"
        );

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID customerId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Paula"));
    }

    @Test
    @Transactional
    void shouldDeleteCustomerSuccessfully() throws Exception {

        CustomerRequestDto request = new CustomerRequestDto(
                "Maria Souza",
                CustomerType.PF,
                "55544433322",
                null,
                "81981112222"
        );

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID customerId = UUID.fromString(objectMapper.readTree(response).get("id").asText());


        List<Account> contas = accountRepository.findAllByCustomerId(customerId);
        for (Account conta : contas) {

            transactionRepository.deleteAllByAccountId(conta.getId());
        }

        accountRepository.deleteAllByCustomerId(customerId);


        mockMvc.perform(delete("/api/v1/customers/" + customerId))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        CustomerRequestDto createRequest = new CustomerRequestDto(
                "Carlos",
                CustomerType.PF,
                "12312312399",
                null,
                "81980001111"
        );

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID customerId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        var updateRequest = new com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO(
                "Carlos Atualizado",
                "81990001111"
        );

        mockMvc.perform(put("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Atualizado"))
                .andExpect(jsonPath("$.phone").value("81990001111"));
    }





}
