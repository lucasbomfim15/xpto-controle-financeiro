package com.xpto.controlefinanceiro.modules.customer.controller;

import com.xpto.controlefinanceiro.common.apiError.ApiErrorResponse;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerRequestDto;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerResponseDTO;
import com.xpto.controlefinanceiro.modules.customer.dtos.CustomerUpdateDTO;
import com.xpto.controlefinanceiro.modules.customer.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Operações relacionadas aos clientes.")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Cria um novo cliente",
            description = "Cria um cliente do tipo PF ou PJ, validando CPF ou CNPJ com base no tipo informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os campos obrigatórios e o tipo de cliente."),
            @ApiResponse(responseCode = "409", description = "CPF ou CNPJ já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        CustomerResponseDTO response = customerService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes retornados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @Operation(summary = "Busca cliente por ID", description = "Retorna os dados do cliente correspondente ao ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(summary = "Deleta cliente", description = "Remove um cliente existente, se não houver vínculos impeditivos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Cliente não pode ser deletado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza cliente", description = "Atualiza nome e telefone do cliente com base no ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable UUID id,
            @RequestBody @Valid CustomerUpdateDTO dto
    ) {
        CustomerResponseDTO updatedCustomer = customerService.update(id, dto);
        return ResponseEntity.ok(updatedCustomer);
    }


    @Operation(summary = "Consulta saldo do cliente", description = "Retorna o saldo do cliente via consulta personalizada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        BigDecimal balance = customerService.getSaldoCliente(id);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getCustomerAddresses(@PathVariable UUID id) {
        List<AddressResponseDTO> addresses = customerService.getAddressesForCustomer(id);
        return ResponseEntity.ok(addresses);
    }



}
