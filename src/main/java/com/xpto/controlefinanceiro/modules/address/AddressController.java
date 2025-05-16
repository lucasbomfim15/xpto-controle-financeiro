package com.xpto.controlefinanceiro.modules.address;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressUpdateDTO;
import com.xpto.controlefinanceiro.modules.address.repository.AddressRepository;
import com.xpto.controlefinanceiro.modules.address.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@RequestBody @Valid AddressRequestDTO dto) {
        AddressResponseDTO created = addressService.create(dto);
        return ResponseEntity.status(201).body(created);
    }


    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(addressService.findById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid AddressUpdateDTO dto) {
        return ResponseEntity.ok(addressService.update(id, dto));
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AddressResponseDTO>> findByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(addressService.findByCustomerId(customerId));
    }


}
