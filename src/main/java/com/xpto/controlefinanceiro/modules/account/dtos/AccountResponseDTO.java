package com.xpto.controlefinanceiro.modules.account.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
        UUID id,
        String bank,
        String agency,
        String number,
        BigDecimal balance,
        BigDecimal initialBalance,
        UUID customerId,
        boolean active
) {}
