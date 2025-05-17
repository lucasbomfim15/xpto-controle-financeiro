package com.xpto.controlefinanceiro.modules.reports.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CompanyRevenueReportDTO(
        String startDate,
        String endDate,
        List<CustomerRevenue> customers,
        BigDecimal totalRevenue
) {
    public record CustomerRevenue(
            String customerName,
            long transactionCount,
            BigDecimal totalAmount
    ) {}
}

