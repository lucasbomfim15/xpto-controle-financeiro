package com.xpto.controlefinanceiro.modules.reports.controller;

import com.xpto.controlefinanceiro.modules.reports.dtos.CompanyRevenueReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalancePeriodReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalanceReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomersBalanceSummaryReportDTO;
import com.xpto.controlefinanceiro.modules.reports.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Rotas relacionadas aos relatórios da aplicação.")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Obter saldo atual de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @GetMapping("/customer/{customerId}/balance")
    public CustomerBalanceReportDTO getCustomerBalance(@PathVariable UUID customerId) {
        return reportService.generateCustomerBalanceReport(customerId);
    }

    @Operation(summary = "Obter saldo de um cliente dentro de um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de saldo por período retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
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

    @Operation(summary = "Resumo de saldo de todos os clientes em uma data específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo de saldos retornado com sucesso")
    })
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


    @Operation(summary = "Relatório de receita da empresa em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de receita gerado com sucesso")
    })
    @GetMapping("/revenue")
    public CompanyRevenueReportDTO getCompanyRevenueReport(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return reportService.generateCompanyRevenueReport(startDate, endDate);
    }
}
