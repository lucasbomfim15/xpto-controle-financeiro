package com.xpto.controlefinanceiro.modules.transaction.dtos;

import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestDTO(
        @NotNull UUID accountId,
        @NotNull TransactionType type,
        @NotNull BigDecimal amount,
        String description
) {
}
