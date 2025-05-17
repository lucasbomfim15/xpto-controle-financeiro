package com.xpto.controlefinanceiro.modules.transaction.mappers;

import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;

import java.time.LocalDateTime;

public class TransactionMapper {

    public static Transaction toEntity(TransactionRequestDTO dto, Account account) {
        return Transaction.builder()
                .account(account)
                .type(dto.type())
                .amount(dto.amount())
                .description(dto.description())
                .date(LocalDateTime.now())
                .build();
    }

    public static TransactionResponseDTO toResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getDescription()
        );
    }
}
