package com.xpto.controlefinanceiro.modules.transaction.controller;


import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.service.TransactionService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Rotas das Transações")
public class TransactionalController {

    private final TransactionService transactionService;

    public TransactionalController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Criar uma nova transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody @Valid TransactionRequestDTO dto) {
        TransactionResponseDTO created = transactionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Listar todas as transações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @Operation(summary = "Listar transações por conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações da conta retornadas com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content)
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.findByAccount(accountId));
    }

    @Operation(summary = "Listar transações por cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações do cliente retornadas com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content)
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(transactionService.findByCustomer(customerId));
    }


}
