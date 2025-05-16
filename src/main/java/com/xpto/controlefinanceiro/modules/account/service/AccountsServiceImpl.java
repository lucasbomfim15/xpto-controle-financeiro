package com.xpto.controlefinanceiro.modules.account.service;

import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;
import com.xpto.controlefinanceiro.modules.account.exceptions.AccountNotFoundException;
import com.xpto.controlefinanceiro.modules.account.mappers.AccountMapper;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountsServiceImpl implements AccountsService {


    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountsServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }



    @Override
    public AccountResponseDTO create(AccountRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + dto.customerId()));

        Account account = AccountMapper.toEntity(dto, customer);
        Account saved = accountRepository.save(account);
        return AccountMapper.toResponseDTO(saved);
    }

    @Override
    public List<AccountResponseDTO> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(AccountMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponseDTO> findByCustomerId(UUID customerId) {
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(AccountMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO findById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        return AccountMapper.toResponseDTO(account);
    }

    @Override
    @Transactional
    public AccountResponseDTO update(UUID id, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));

//        Implementar isso após terminar a entidade de transactions!!!!
//        if (transactionRepository.existsByAccountId(id)) {
//            throw new IllegalStateException("Cannot update account with existing transactions.");
//        }


        account.setBank(dto.bank());
        account.setAgency(dto.agency());
        account.setNumber(dto.number());
        account.setBalance(dto.balance());

        Account updated = accountRepository.save(account);
        return AccountMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));

        // Exclusão lógica
        account.setActive(false);
        accountRepository.save(account);
    }






}
