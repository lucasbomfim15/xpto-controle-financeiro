package com.xpto.controlefinanceiro.modules.reports.services;


import com.xpto.controlefinanceiro.modules.reports.dtos.CompanyRevenueReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalancePeriodReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalanceReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomersBalanceSummaryReportDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    CustomerBalanceReportDTO generateCustomerBalanceReport(UUID customerId);
    CustomerBalancePeriodReportDTO generateCustomerBalancePeriodReport(UUID customerId, LocalDate startDate, LocalDate endDate);
    CustomersBalanceSummaryReportDTO generateSummaryReport(LocalDate date);
    CompanyRevenueReportDTO generateCompanyRevenueReport(LocalDate startDate, LocalDate endDate);


}
