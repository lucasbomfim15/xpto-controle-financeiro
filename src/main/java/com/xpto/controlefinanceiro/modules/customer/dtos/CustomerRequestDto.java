package com.xpto.controlefinanceiro.modules.customer.dtos;

import com.xpto.controlefinanceiro.common.validation.ValidCustomer;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de cliente")
@ValidCustomer
public record CustomerRequestDto(
        @NotBlank(message = "Name is mandatory")
        @Schema(description = "Nome do cliente", example = "João da Silva")
        String name,

        @NotNull(message = "Customer type is mandatory")
        @Schema(description = "Tipo do cliente: PF (Pessoa Física) ou PJ (Pessoa Jurídica)", example = "PF")
        CustomerType customerType,

        @Schema(description = "CPF do cliente (obrigatório para PF)", example = "12345678900")
        String cpf,

        @Schema(description = "CNPJ do cliente (obrigatório para PJ)", example = "12345678000199")
        String cnpj,

        @NotBlank(message = "Phone is mandatory")
        @Schema(description = "Telefone para contato", example = "(81) 99999-9999")
        String phone
) {
}
