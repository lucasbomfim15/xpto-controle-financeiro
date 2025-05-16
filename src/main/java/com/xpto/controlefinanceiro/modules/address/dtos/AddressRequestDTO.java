package com.xpto.controlefinanceiro.modules.address.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddressRequestDTO(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipCode,
        @NotNull UUID customerId
) {}
