package com.xpto.controlefinanceiro.modules.reports.controller;

import com.xpto.controlefinanceiro.modules.reports.dtos.CompanyRevenueReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalancePeriodReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalanceReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomersBalanceSummaryReportDTO;
import com.xpto.controlefinanceiro.modules.reports.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/customer/{customerId}/balance")
    public CustomerBalanceReportDTO getCustomerBalance(@PathVariable UUID customerId) {
        return reportService.generateCustomerBalanceReport(customerId);
    }


    @GetMapping("/customer/{customerId}/balance-period")
    public CustomerBalancePeriodReportDTO getCustomerBalancePeriod(
            @PathVariable UUID customerId,
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return reportService.generateCustomerBalancePeriodReport(customerId, startDate, endDate);
    }


    @GetMapping("/customers/balance-summary")
    public CustomersBalanceSummaryReportDTO getBalanceSummary(
            @RequestParam(required = false) String date
    ) {
        LocalDate reportDate;
        if (date == null || date.isEmpty()) {
            reportDate = LocalDate.now();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            reportDate = LocalDate.parse(date, formatter);
        }

        return reportService.generateSummaryReport(reportDate);
    }


    @GetMapping("/revenue")
    public CompanyRevenueReportDTO getCompanyRevenueReport(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return reportService.generateCompanyRevenueReport(startDate, endDate);
    }
}
