package com.xpto.controlefinanceiro.modules.customer.dtos;

import com.xpto.controlefinanceiro.common.validation.ValidCustomer;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidCustomer
public record CustomerRequestDto(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotNull(message = "Customer type is mandatory")
        CustomerType customerType,

        String cpf,

        String cnpj,

        @NotBlank(message = "Phone is mandatory")
        String phone
) {
}
