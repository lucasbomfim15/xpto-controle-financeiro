package com.xpto.controlefinanceiro.modules.address.services;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressUpdateDTO;
import com.xpto.controlefinanceiro.modules.address.exceptions.AddressNotFoundException;
import com.xpto.controlefinanceiro.modules.address.mappers.AddressMapper;
import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.address.repository.AddressRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressServiceImpl(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    public AddressResponseDTO create(AddressRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + dto.customerId()));

        Address address = AddressMapper.toEntity(dto, customer);
        Address saved = addressRepository.save(address);
        return AddressMapper.toResponseDTO(saved);
    }

    @Override
    public List<AddressResponseDTO> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(AddressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressResponseDTO> findByCustomerId(UUID customerId) {
        return addressRepository.findByCustomerId(customerId)
                .stream()
                .map(AddressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDTO findById(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));
        return AddressMapper.toResponseDTO(address);
    }

    @Override
    @Transactional
    public AddressResponseDTO update(UUID id, AddressUpdateDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));

        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipCode(dto.zipCode());

        Address updated = addressRepository.save(address);
        return AddressMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + id));
        addressRepository.delete(address);
    }






}
