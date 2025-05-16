package com.xpto.controlefinanceiro.modules.address.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddressUpdateDTO(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipCode
) {}
