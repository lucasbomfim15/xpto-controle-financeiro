package com.xpto.controlefinanceiro.modules.account.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountUpdateDTO(
        @NotBlank String bank,
        @NotBlank String agency,
        @NotBlank String number,
        @NotNull BigDecimal balance
) {}
