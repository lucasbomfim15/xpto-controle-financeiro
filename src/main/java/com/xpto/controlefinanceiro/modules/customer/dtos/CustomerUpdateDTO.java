package com.xpto.controlefinanceiro.modules.customer.dtos;

import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateDTO(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Phone is mandatory")
        String phone
) {
}
