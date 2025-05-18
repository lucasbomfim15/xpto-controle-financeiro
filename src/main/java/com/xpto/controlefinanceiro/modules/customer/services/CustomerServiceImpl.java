package com.xpto.controlefinanceiro.modules.customer.services;

import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CnpjAlreadyExistsException;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CpfAlreadyExistsException;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerDeletionException;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.mappers.CustomerMapper;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final InitialCustomerSetupService initialCustomerSetupService;


    public CustomerServiceImpl(CustomerRepository repository, ModelMapper modelMapper,
                               InitialCustomerSetupService initialCustomerSetupService, AccountRepository accountRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.initialCustomerSetupService = initialCustomerSetupService;
        this.accountRepository = accountRepository;
    }

    @Override
    public CustomerResponseDTO create(@Valid CustomerRequestDto dto) {
        // Verifica duplicidade de CPF
        if (dto.customerType() == CustomerType.PF && StringUtils.hasText(dto.cpf())) {
            repository.findByCpf(dto.cpf()).ifPresent(c -> {
                throw new CpfAlreadyExistsException("CPF already exists");
            });
        }

        // Verifica duplicidade de CNPJ
        if (dto.customerType() == CustomerType.PJ && StringUtils.hasText(dto.cnpj())) {
            repository.findByCnpj(dto.cnpj()).ifPresent(c -> {
                throw new CnpjAlreadyExistsException("CNPJ already exists");
            });
        }

        // Mapeia DTO para entidade com mapper separado
        Customer customer = CustomerMapper.toEntity(dto);

        // Salva no banco
        Customer saved = repository.save(customer);

        // Chama serviço para criar conta e movimentação inicial
        initialCustomerSetupService.setupInitialAccountAndTransaction(saved);

        // Retorna DTO de resposta
        return CustomerMapper.toResponseDTO(saved);
    }

    @Override
    public List<CustomerResponseDTO> findAll() {
        List<Customer> customers = repository.findAll();

        return customers.stream()
                .map(CustomerMapper::toResponseDTO)
                .collect(Collectors.toList());

    }

    @Override
    public CustomerResponseDTO findById(UUID id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return CustomerMapper.toResponseDTO(customer);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }

        int countAccounts = accountRepository.countByCustomerId(id);

        if (countAccounts > 0) {
            throw new CustomerDeletionException("Não é permitido deletar cliente que possui contas associadas.");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public CustomerResponseDTO update(UUID id, CustomerUpdateDTO dto) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        customer.setName(dto.name());
        customer.setPhone(dto.phone());

        Customer updated = repository.save(customer);
        return CustomerMapper.toResponseDTO(updated);
    }


}
