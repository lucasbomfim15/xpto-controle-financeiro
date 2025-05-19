package com.xpto.controlefinanceiro.modules.address;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressRequestDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.dtos.AddressUpdateDTO;
import com.xpto.controlefinanceiro.modules.address.repository.AddressRepository;
import com.xpto.controlefinanceiro.modules.address.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Addresses", description = "Rotas para gerenciamento de endereços.")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    @Operation(summary = "Cria um novo endereço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@RequestBody @Valid AddressRequestDTO dto) {
        AddressResponseDTO created = addressService.create(dto);
        return ResponseEntity.status(201).body(created);
    }


    @Operation(summary = "Lista todos os endereços cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @Operation(summary = "Busca um endereço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(addressService.findById(id));
    }


    @Operation(summary = "Deleta um endereço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Atualiza um endereço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid AddressUpdateDTO dto) {
        return ResponseEntity.ok(addressService.update(id, dto));
    }

    @Operation(summary = "Lista endereços por ID de cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereços do cliente retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AddressResponseDTO>> findByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(addressService.findByCustomerId(customerId));
    }


}
