package com.xpto.controlefinanceiro.modules.account.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
        @NotBlank String bank,
        @NotBlank String agency,
        @NotBlank String number,
        @NotNull BigDecimal balance,
        @NotNull UUID customerId
) {}
