package com.xpto.controlefinanceiro.modules.customer.controller;

import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO;
import com.xpto.controlefinanceiro.modules.customer.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        CustomerResponseDTO response = customerService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable UUID id,
            @RequestBody @Valid CustomerUpdateDTO dto
    ) {
        CustomerResponseDTO updatedCustomer = customerService.update(id, dto);
        return ResponseEntity.ok(updatedCustomer);
    }


    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        BigDecimal balance = customerService.getSaldoCliente(id);
        return ResponseEntity.ok(balance);
    }



}
