package com.xpto.controlefinanceiro.modules.customer.services;

import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CnpjAlreadyExistsException;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CpfAlreadyExistsException;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.mappers.CustomerMapper;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepository repository;
    private AccountRepository accountRepository;
    private InitialCustomerSetupService initialSetupService;
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        accountRepository = mock(AccountRepository.class);  // novo mock para AccountRepository
        initialSetupService = mock(InitialCustomerSetupService.class);
        service = new CustomerServiceImpl(repository, initialSetupService, accountRepository);
    }

    @Test
    void shouldCreateCustomerWithCpf() {
        CustomerRequestDto dto = new CustomerRequestDto("João", CustomerType.PF, "12345678900", null, "11999999999");

        when(repository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CustomerResponseDTO response = service.create(dto);

        assertEquals("João", response.name());
        verify(initialSetupService, times(1)).setupInitialAccountAndTransaction(any());
    }

    @Test
    void shouldThrowCpfAlreadyExistsException() {
        CustomerRequestDto dto = new CustomerRequestDto("Maria", CustomerType.PF, "11111111111", null, "11888888888");

        when(repository.findByCpf("11111111111")).thenReturn(Optional.of(new Customer()));

        assertThrows(CpfAlreadyExistsException.class, () -> service.create(dto));
    }

    @Test
    void shouldThrowCnpjAlreadyExistsException() {
        CustomerRequestDto dto = new CustomerRequestDto("Empresa X", CustomerType.PJ, null, "12345678000100", "1133333333");

        when(repository.findByCnpj("12345678000100")).thenReturn(Optional.of(new Customer()));

        assertThrows(CnpjAlreadyExistsException.class, () -> service.create(dto));
    }


    @Test
    void shouldReturnAllCustomers() {
        List<Customer> mockList = List.of(
                Customer.builder().id(UUID.randomUUID()).name("João").phone("123").customerType(CustomerType.PF).build(),
                Customer.builder().id(UUID.randomUUID()).name("Maria").phone("456").customerType(CustomerType.PJ).build()
        );

        when(repository.findAll()).thenReturn(mockList);

        List<CustomerResponseDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("João", result.get(0).name());
        assertEquals("Maria", result.get(1).name());
    }

    @Test
    void shouldReturnCustomerById() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).name("Lucas").phone("111").customerType(CustomerType.PF).build();

        when(repository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponseDTO response = service.findById(id);

        assertEquals("Lucas", response.name());
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionOnFindById() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void shouldDeleteCustomerById() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteById(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionOnDelete() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> service.deleteById(id));
    }

    @Test
    void shouldUpdateCustomer() {
        UUID id = UUID.randomUUID();
        Customer existing = Customer.builder()
                .id(id)
                .name("Velho Nome")
                .phone("000000000")
                .customerType(CustomerType.PF)
                .build();

        CustomerUpdateDTO update = new CustomerUpdateDTO("Novo Nome", "111111111");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponseDTO response = service.update(id, update);

        assertEquals("Novo Nome", response.name());
        assertEquals("111111111", response.phone());
    }
}
