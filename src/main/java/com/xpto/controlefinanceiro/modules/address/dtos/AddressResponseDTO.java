package com.xpto.controlefinanceiro.modules.address.dtos;

import java.util.UUID;

public record AddressResponseDTO(
        UUID id,
        String street,
        String city,
        String state,
        String zipCode,
        UUID customerId
) {}
