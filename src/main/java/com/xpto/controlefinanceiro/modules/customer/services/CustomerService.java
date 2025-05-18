package com.xpto.controlefinanceiro.modules.customer.services;

import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponseDTO create(CustomerRequestDto dto);
    List<CustomerResponseDTO> findAll();
    CustomerResponseDTO findById(UUID id);
    void deleteById(UUID id);
    CustomerResponseDTO update(UUID id,CustomerUpdateDTO dto);
    BigDecimal getSaldoCliente(UUID customerId);

}
