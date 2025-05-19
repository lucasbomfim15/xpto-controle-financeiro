package com.xpto.controlefinanceiro.modules.account.controller;

import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;
import com.xpto.controlefinanceiro.modules.account.service.AccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operações relacionadas às contas bancárias dos clientes.")
public class AccountsController {

    private final AccountsService accountService;

    public AccountsController(AccountsService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Cria uma nova conta",
            description = "Cria uma nova conta bancária para um cliente previamente cadastrado no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação da conta"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@RequestBody @Valid AccountRequestDTO dto) {
        AccountResponseDTO created = accountService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Lista todas as contas",
            description = "Retorna uma lista com todas as contas cadastradas no sistema, independentemente do cliente."
    )
    @ApiResponse(responseCode = "200", description = "Contas retornadas com sucesso")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @Operation(
            summary = "Busca contas por cliente",
            description = "Retorna todas as contas bancárias associadas ao cliente informado via ID."
    )
    @ApiResponse(responseCode = "200", description = "Contas do cliente retornadas com sucesso")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponseDTO>> findByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(accountService.findByCustomerId(customerId));
    }


    @Operation(
            summary = "Busca conta por ID",
            description = "Busca os detalhes de uma conta específica com base no seu identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @Operation(
            summary = "Atualiza os dados de uma conta",
            description = "Atualiza os dados de uma conta existente, como banco, agência, número e saldo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid AccountUpdateDTO dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }


    @Operation(
            summary = "Deleta uma conta (exclusão lógica)",
            description = "Marca a conta como inativa em vez de removê-la do banco de dados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
