package com.xpto.controlefinanceiro.modules.reports.mapper;

import com.xpto.controlefinanceiro.modules.address.model.Address;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalancePeriodReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalanceReportDTO;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportMapper {

    public static CustomerBalanceReportDTO toCustomerBalanceReport(
            Customer customer,
            List<Transaction> transactions,
            BigDecimal initialBalance,
            BigDecimal currentBalance
    ) {
        // Contagem de transações do tipo crédito
        long creditCount = transactions.stream()
                .filter(t -> t.getType() == TransactionType.CREDIT)
                .count();

        // Contagem de transações do tipo débito
        long debitCount = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEBIT)
                .count();

        // Cálculo da taxa total paga
        BigDecimal totalFees = calculateTotalFee(customer, transactions);

        // Formatação da data de registro do cliente
        String formattedDate = customer.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Formatação do endereço ou mensagem padrão
        String address = customer.getAddresses().isEmpty()
                ? "Endereço não informado"
                : formatAddress(customer.getAddresses().get(0));

        // Construção do DTO
        return CustomerBalanceReportDTO.builder()
                .customerName(customer.getName())
                .customerSince(formattedDate)
                .address(address)
                .creditMovements(creditCount)
                .debitMovements(debitCount)
                .totalMovements(transactions.size())
                .feePaid(totalFees)
                .initialBalance(initialBalance)
                .currentBalance(currentBalance)
                .build();
    }

    public static CustomerBalancePeriodReportDTO toCustomerBalancePeriodReport(
            Customer customer,
            List<Transaction> transactions,
            BigDecimal initialBalance,
            BigDecimal currentBalance,
            LocalDate startDate,
            LocalDate endDate
    ) {
        long creditCount = transactions.stream()
                .filter(t -> t.getType() == TransactionType.CREDIT)
                .count();

        long debitCount = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEBIT)
                .count();

        // Reutilizando o método existente
        BigDecimal totalFees = calculateTotalFee(customer, transactions);

        String address = customer.getAddresses().isEmpty()
                ? "Endereço não informado"
                : formatAddress(customer.getAddresses().get(0));

        String period = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " a " +
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new CustomerBalancePeriodReportDTO(
                period,
                customer.getName(),
                customer.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                address,
                creditCount,
                debitCount,
                transactions.size(),
                totalFees,
                initialBalance,
                currentBalance
        );
    }





    /**
     * Método para calcular a taxa total paga com base nas transações
     */
    private static BigDecimal calculateTotalFee(Customer customer, List<Transaction> transactions) {
        // Variável inicial para data de início
        LocalDate startDate = customer.getCreatedAt();
        BigDecimal total = BigDecimal.ZERO;

        // Itera sobre os intervalos de 30 dias enquanto a data inicial estiver no passado
        while (!startDate.isAfter(LocalDate.now())) {
            final LocalDate currentStartDate = startDate; // Variável imutável para captura no lambda
            LocalDate endDate = currentStartDate.plusDays(30);

            // Conta as transações realizadas dentro do intervalo de 30 dias
            long count = transactions.stream()
                    .filter(t -> {
                        LocalDate date = t.getDate().toLocalDate();
                        return !date.isBefore(currentStartDate) && date.isBefore(endDate);
                    })
                    .count();

            // Calcula a taxa por transação baseada no número de transações
            BigDecimal feePerTransaction;
            if (count <= 10) {
                feePerTransaction = BigDecimal.valueOf(1.00);
            } else if (count <= 20) {
                feePerTransaction = BigDecimal.valueOf(0.75);
            } else {
                feePerTransaction = BigDecimal.valueOf(0.50);
            }

            // Incrementa o total acumulado das taxas
            total = total.add(feePerTransaction.multiply(BigDecimal.valueOf(count)));

            // Move o intervalo para os próximos 30 dias
            startDate = endDate;
        }

        return total; // Retorna o valor total das taxas pagas
    }

    /**
     * Método para formatar o endereço
     */
    private static String formatAddress(Address a) {
        // Formatação do endereço no padrão desejado
        return String.format("%s, %s, %s, %s",
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getZipCode());
    }
}