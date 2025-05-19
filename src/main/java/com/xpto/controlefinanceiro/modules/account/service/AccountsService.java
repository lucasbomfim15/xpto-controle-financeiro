package com.xpto.controlefinanceiro.modules.account.service;

import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface AccountsService {
    AccountResponseDTO create(AccountRequestDTO dto);
    List<AccountResponseDTO> findAll();
    List<AccountResponseDTO> findByCustomerId(UUID customerId);
    AccountResponseDTO findById(UUID id);
    AccountResponseDTO update(UUID id, AccountUpdateDTO dto);
    void delete(UUID id);
}
