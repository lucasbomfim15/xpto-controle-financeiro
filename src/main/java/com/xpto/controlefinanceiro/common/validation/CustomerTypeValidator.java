package com.xpto.controlefinanceiro.common.validation;


import com.xpto.controlefinanceiro.common.validation.ValidCustomer;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.enums.CustomerType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerTypeValidator implements ConstraintValidator<ValidCustomer, CustomerRequestDto> {

    @Override
    public boolean isValid(CustomerRequestDto dto, ConstraintValidatorContext context) {
        if (dto.customerType() == CustomerType.PF) {
            boolean valid = dto.cpf() != null && !dto.cpf().isBlank();
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("CPF is required for Pessoa Física")
                        .addConstraintViolation();
            }
            return valid;
        } else if (dto.customerType() == CustomerType.PJ) {
            boolean valid = dto.cnpj() != null && !dto.cnpj().isBlank();
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("CNPJ is required for Pessoa Jurídica")
                        .addConstraintViolation();
            }
            return valid;
        }
        return false;
    }

}
