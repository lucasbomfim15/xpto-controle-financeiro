package com.xpto.controlefinanceiro.modules.address.mappers;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;

public class AddressMapper {

    public static Address toEntity(AddressRequestDTO dto, Customer customer) {
        Address address = new Address();
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipCode(dto.zipCode());
        address.setCustomer(customer);
        return address;
    }

    public static AddressResponseDTO toResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCustomer().getId()
        );
    }
}
