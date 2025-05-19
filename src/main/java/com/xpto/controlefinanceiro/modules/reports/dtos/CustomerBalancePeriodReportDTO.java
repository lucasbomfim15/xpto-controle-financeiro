package com.xpto.controlefinanceiro.modules.reports.dtos;

import java.math.BigDecimal;

public record CustomerBalancePeriodReportDTO(
        String period,
        String customerName,
        String customerSince,
        String address,
        long creditMovements,
        long debitMovements,
        int totalMovements,
        BigDecimal feePaid,
        BigDecimal initialBalance,
        BigDecimal currentBalance
) {}
