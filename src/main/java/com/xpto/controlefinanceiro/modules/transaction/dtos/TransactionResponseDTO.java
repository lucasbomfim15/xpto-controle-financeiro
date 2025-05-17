package com.xpto.controlefinanceiro.modules.transaction.dtos;


import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        LocalDateTime date,
        String description
) {
}
