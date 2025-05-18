package com.xpto.controlefinanceiro.modules.address.services;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressUpdateDTO;
import com.xpto.controlefinanceiro.modules.address.exceptions.AddressNotFoundException;
import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.address.repository.AddressRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    private AddressRepository addressRepository;
    private CustomerRepository customerRepository;
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        customerRepository = mock(CustomerRepository.class);
        addressService = new AddressServiceImpl(addressRepository, customerRepository);
    }

    @Test
    void shouldCreateAddressWhenCustomerExists() {
        UUID customerId = UUID.randomUUID();
        AddressRequestDTO dto = new AddressRequestDTO("Rua A", "Cidade B", "SP", "12345-000", customerId);
        Customer customer = Customer.builder().id(customerId).name("Lucas").build();
        Address address = Address.builder().id(UUID.randomUUID()).street("Rua A").city("Cidade B").state("SP").zipCode("12345-000").customer(customer).build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        var response = addressService.create(dto);

        assertNotNull(response);
        assertEquals("Rua A", response.street());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void shouldThrowWhenCustomerNotFoundOnCreate() {
        UUID customerId = UUID.randomUUID();
        AddressRequestDTO dto = new AddressRequestDTO("Rua A", "Cidade B", "SP", "12345-000", customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> addressService.create(dto));
    }

    @Test
    void shouldFindAllAddresses() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .street("Rua A")
                .city("Cidade B")
                .state("SP")
                .zipCode("12345-000")
                .customer(customer)
                .build();

        when(addressRepository.findAll()).thenReturn(List.of(address));

        var result = addressService.findAll();

        assertEquals(1, result.size());
        assertEquals("Rua A", result.get(0).street());
    }

    @Test
    void shouldFindByCustomerId() {
        UUID customerId = UUID.randomUUID();
        Address address = Address.builder().id(UUID.randomUUID()).street("Rua B").customer(Customer.builder().id(customerId).build()).build();

        when(addressRepository.findByCustomerId(customerId)).thenReturn(List.of(address));

        var result = addressService.findByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals("Rua B", result.get(0).street());
    }

    @Test
    void shouldFindAddressById() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        UUID id = UUID.randomUUID();
        Address address = Address.builder()
                .id(id)
                .street("Rua C")
                .customer(customer)
                .build();

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        var result = addressService.findById(id);

        assertEquals("Rua C", result.street());
    }

    @Test
    void shouldThrowWhenAddressNotFoundById() {
        UUID id = UUID.randomUUID();

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.findById(id));
    }

    @Test
    void shouldUpdateAddress() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        UUID id = UUID.randomUUID();
        Address address = Address.builder()
                .id(id)
                .street("Old Street")
                .customer(customer) // <-- Necessário
                .build();
        AddressUpdateDTO dto = new AddressUpdateDTO("New Street", "New City", "RJ", "99999-000");

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        var result = addressService.update(id, dto);

        assertEquals("New Street", result.street());
        verify(addressRepository).save(address);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentAddress() {
        UUID id = UUID.randomUUID();
        AddressUpdateDTO dto = new AddressUpdateDTO("New", "City", "SP", "00000-000");

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.update(id, dto));
    }

    @Test
    void shouldDeleteAddress() {
        UUID id = UUID.randomUUID();
        Address address = Address.builder().id(id).build();

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        addressService.delete(id);

        verify(addressRepository).delete(address);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentAddress() {
        UUID id = UUID.randomUUID();

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.delete(id));
    }
}
