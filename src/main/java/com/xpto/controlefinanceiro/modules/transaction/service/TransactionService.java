package com.xpto.controlefinanceiro.modules.transaction.service;

import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    List<TransactionResponseDTO> findAll();

    List<TransactionResponseDTO> findByAccount(UUID accountId);

    List<TransactionResponseDTO> findByCustomer(UUID customerId);

    TransactionResponseDTO create(TransactionRequestDTO dto);
}
