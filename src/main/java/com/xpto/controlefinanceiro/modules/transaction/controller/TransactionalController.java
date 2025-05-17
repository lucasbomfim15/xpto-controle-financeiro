package com.xpto.controlefinanceiro.modules.transaction.controller;


import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionRequestDTO;
import com.xpto.controlefinanceiro.modules.transaction.dtos.TransactionResponseDTO;
import com.xpto.controlefinanceiro.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionalController {

    private final TransactionService transactionService;

    public TransactionalController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody @Valid TransactionRequestDTO dto) {
        TransactionResponseDTO created = transactionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.findByAccount(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(transactionService.findByCustomer(customerId));
    }


}
