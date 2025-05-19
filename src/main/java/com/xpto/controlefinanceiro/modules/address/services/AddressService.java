package com.xpto.controlefinanceiro.modules.address.services;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponseDTO create(AddressRequestDTO dto);
    List<AddressResponseDTO> findAll();
    List<AddressResponseDTO> findByCustomerId(UUID customerId);
    AddressResponseDTO findById(UUID id);
    AddressResponseDTO update(UUID id, AddressUpdateDTO dto);
    void delete(UUID id);
}
