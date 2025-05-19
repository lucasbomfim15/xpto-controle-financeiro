package com.xpto.controlefinanceiro.modules.customer.mappers;

import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerRequestDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        customer.setCustomerType(dto.customerType());

        if (dto.customerType().name().equals("PF")) {
            customer.setCpf(dto.cpf());
            customer.setCnpj(null);
        } else {
            customer.setCnpj(dto.cnpj());
            customer.setCpf(null);
        }

        return customer;
    }

    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getCustomerType(),
                customer.getCpf(),
                customer.getCnpj(),
                customer.getPhone(),
                customer.getCreatedAt()
        );
    }
}
