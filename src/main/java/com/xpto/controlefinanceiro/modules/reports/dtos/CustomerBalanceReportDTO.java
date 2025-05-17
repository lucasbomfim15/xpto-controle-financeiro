package com.xpto.controlefinanceiro.modules.reports.dtos;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CustomerBalanceReportDTO(
        String customerName,
        String customerSince,
        String address,
        long creditMovements,
        long debitMovements,
        long totalMovements,
        BigDecimal feePaid,
        BigDecimal initialBalance,
        BigDecimal currentBalance
) {}
