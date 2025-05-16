package com.xpto.controlefinanceiro.modules.customer.dtos;

import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;

import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        CustomerType customerType,
        String cpf,
        String cnpj,
        String phone
) {
}
