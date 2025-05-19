package com.xpto.controlefinanceiro.modules.address.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.address.repository.AddressRepository;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID customerId;


    private Address createAddress(String street, String city, String state, String zipCode) {
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setCustomer(customerRepository.findById(customerId).orElseThrow());
        return addressRepository.save(address);
    }

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        addressRepository.deleteAll();
        customerRepository.deleteAll();

        // Recria o customer de teste para cada método
        customerId = customerRepository.save(Customer.builder()
                .name("Cliente de Teste")
                .customerType(CustomerType.PF)
                .phone("81983670589")
                .cpf("12345678900")
                .build()).getId();
    }

    @Test
    void shouldCreateAddressSuccessfully() throws Exception {
        AddressRequestDTO request = new AddressRequestDTO(
                "Rua A",
                "Recife",
                "PE",
                "50000-000",
                customerId
        );

        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Rua A"))
                .andExpect(jsonPath("$.city").value("Recife"))
                .andExpect(jsonPath("$.state").value("PE"))
                .andExpect(jsonPath("$.zipCode").value("50000-000"))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    void shouldReturn400WhenCreatingAddressWithMissingFields() throws Exception {
        AddressRequestDTO invalidDTO = new AddressRequestDTO(
                null,  // street
                "Recife",
                "PE",
                "50000-000",
                customerId
        );

        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnListOfAddresses() throws Exception {
        Address address = createAddress("Rua 1", "Olinda", "PE", "53000-000");
        Address address2 = createAddress("Rua 2", "Olinda", "PE", "53300-000");


        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(address.getId().toString()));
    }

    @Test
    void shouldReturnAddressById() throws Exception {
        Address address = createAddress("Rua 2", "Jaboatão", "PE", "54000-000");

        mockMvc.perform(get("/api/v1/addresses/" + address.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Rua 2"));
    }

    @Test
    void shouldReturn404WhenAddressNotFoundById() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/addresses/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAddressSuccessfully() throws Exception {
        Address address = createAddress("Antiga Rua", "Olinda", "PE", "53000-000");

        AddressRequestDTO updatedRequest = new AddressRequestDTO(
                "Nova Rua",
                "Recife",
                "PE",
                "50000-000",
                customerId
        );

        mockMvc.perform(put("/api/v1/addresses/" + address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Nova Rua"))
                .andExpect(jsonPath("$.city").value("Recife"));
    }

    @Test
    void shouldReturn400WhenUpdatingAddressWithInvalidData() throws Exception {
        Address address = createAddress("Rua Teste", "Recife", "PE", "50000-000");

        AddressRequestDTO invalidUpdate = new AddressRequestDTO(
                "",  // street vazio
                "Recife",
                "PE",
                "50000-000",
                customerId
        );

        mockMvc.perform(put("/api/v1/addresses/" + address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentAddress() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        AddressRequestDTO updateDTO = new AddressRequestDTO(
                "Rua Falsa", "Recife", "PE", "50000-000", customerId
        );

        mockMvc.perform(put("/api/v1/addresses/" + nonexistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldDeleteAddressSuccessfully() throws Exception {
        Address address = createAddress("Rua para deletar", "Paulista", "PE", "53400-000");

        mockMvc.perform(delete("/api/v1/addresses/" + address.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentAddress() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/addresses/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAddressesByCustomerId() throws Exception {
        Address address1 = createAddress("Rua A", "Recife", "PE", "50000-000");
        Address address2 = createAddress("Rua B", "Recife", "PE", "50001-000");

        mockMvc.perform(get("/api/v1/addresses/customer/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoAddresses() throws Exception {
        UUID randomCustomerId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/addresses/customer/" + randomCustomerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }






}
