package com.xpto.controlefinanceiro.modules.reports.dtos;

import java.math.BigDecimal;

public record CustomersBalanceSummaryReportDTO(
        String reportDate,
        java.util.List<CustomerBalanceSummary> customers
) {
    public record CustomerBalanceSummary(
            String customerName,
            String customerSince,
            BigDecimal balance
    ) {}
}
