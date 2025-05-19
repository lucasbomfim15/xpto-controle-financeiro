package com.xpto.controlefinanceiro.modules.reports.services;

import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.account.repository.AccountRepository;
import com.xpto.controlefinanceiro.modules.customer.exceptions.CustomerNotFoundException;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;
import com.xpto.controlefinanceiro.modules.customer.repository.CustomerRepository;
import com.xpto.controlefinanceiro.modules.reports.dtos.CompanyRevenueReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalancePeriodReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomerBalanceReportDTO;
import com.xpto.controlefinanceiro.modules.reports.dtos.CustomersBalanceSummaryReportDTO;
import com.xpto.controlefinanceiro.modules.reports.mapper.ReportMapper;
import com.xpto.controlefinanceiro.modules.reports.services.ReportService;
import com.xpto.controlefinanceiro.modules.transaction.enums.TransactionType;
import com.xpto.controlefinanceiro.modules.transaction.model.Transaction;
import com.xpto.controlefinanceiro.modules.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public CustomerBalanceReportDTO generateCustomerBalanceReport(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        List<Account> accounts = accountRepository.findByCustomerId(customerId);

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma conta encontrada para o cliente");
        }

        // Considerar uma única conta por cliente, ou somar todas.
        BigDecimal initialBalance = BigDecimal.ZERO;
        BigDecimal currentBalance = BigDecimal.ZERO;
        List<Transaction> allTransactions = new java.util.ArrayList<>();

        for (Account account : accounts) {
            initialBalance = initialBalance.add(account.getInitialBalance());
            currentBalance = currentBalance.add(account.getBalance());
            List<Transaction> accountTransactions = transactionRepository.findByAccountId(account.getId());
            allTransactions.addAll(accountTransactions);
        }

        return ReportMapper.toCustomerBalanceReport(customer, allTransactions, initialBalance, currentBalance);
    }

    @Override
    public CustomerBalancePeriodReportDTO generateCustomerBalancePeriodReport(UUID customerId, LocalDate startDate, LocalDate endDate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        List<Account> accounts = accountRepository.findByCustomerId(customerId);

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma conta encontrada para o cliente");
        }

        BigDecimal initialBalance = BigDecimal.ZERO;
        BigDecimal currentBalance = BigDecimal.ZERO;
        List<Transaction> filteredTransactions = new java.util.ArrayList<>();

        for (Account account : accounts) {
            initialBalance = initialBalance.add(account.getInitialBalance());
            currentBalance = currentBalance.add(account.getBalance());

            // Filtra transações do período desejado
            List<Transaction> transactions = transactionRepository.findByAccountId(account.getId()).stream()
                    .filter(t -> {
                        LocalDate date = t.getDate().toLocalDate();
                        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                                (date.isEqual(endDate) || date.isBefore(endDate));
                    })
                    .toList();

            filteredTransactions.addAll(transactions);
        }

        return ReportMapper.toCustomerBalancePeriodReport(customer, filteredTransactions, initialBalance, currentBalance, startDate, endDate);
    }

    @Override
    public CustomersBalanceSummaryReportDTO generateSummaryReport(LocalDate date) {
        List<Customer> customers = customerRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);

        LocalDate finalDate = date; // data de referência para o relatório

        List<CustomersBalanceSummaryReportDTO.CustomerBalanceSummary> summaries = customers.stream()
                .map(customer -> {
                    List<Account> accounts = accountRepository.findByCustomerId(customer.getId());

                    BigDecimal totalBalanceUntilDate = BigDecimal.ZERO;

                    for (Account account : accounts) {
                        BigDecimal initialBalance = account.getInitialBalance();

                        // Buscar transações até a data final (inclusive)
                        // Para isso, vamos precisar de um método no transactionRepository para isso:
                        // List<Transaction> findByAccountIdAndDateLessThanEqual(UUID accountId, LocalDateTime date);
                        // Como você tem no seu filtro de período, mas adaptado para até date.

                        LocalDateTime endOfDay = finalDate.atTime(23, 59, 59);

                        List<Transaction> transactionsUntilDate = transactionRepository
                                .findByAccountIdAndDateLessThanEqual(account.getId(), endOfDay);

                        // Somar créditos e débitos até a data
                        BigDecimal credits = transactionsUntilDate.stream()
                                .filter(t -> t.getType() == TransactionType.CREDIT)
                                .map(Transaction::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal debits = transactionsUntilDate.stream()
                                .filter(t -> t.getType() == TransactionType.DEBIT)
                                .map(Transaction::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal balanceUntilDate = initialBalance.add(credits).subtract(debits);

                        totalBalanceUntilDate = totalBalanceUntilDate.add(balanceUntilDate);
                    }

                    return new CustomersBalanceSummaryReportDTO.CustomerBalanceSummary(
                            customer.getName(),
                            customer.getCreatedAt().format(formatter),
                            totalBalanceUntilDate
                    );
                })
                .collect(Collectors.toList());

        return new CustomersBalanceSummaryReportDTO(formattedDate, summaries);
    }

    @Override
    public CompanyRevenueReportDTO generateCompanyRevenueReport(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        List<Customer> customers = customerRepository.findAll();
        List<CompanyRevenueReportDTO.CustomerRevenue> customerRevenues = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Customer customer : customers) {
            List<Account> accounts = accountRepository.findByCustomerId(customer.getId());

            long transactionCount = 0;
            BigDecimal customerTotal = BigDecimal.ZERO;

            for (Account account : accounts) {
                List<Transaction> transactions = transactionRepository.findByAccountId(account.getId()).stream()
                        .filter(t -> {
                            LocalDate txDate = t.getDate().toLocalDate();
                            return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                        })
                        .toList();

                transactionCount += transactions.size();
                customerTotal = customerTotal.add(
                        transactions.stream()
                                .map(Transaction::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                );
            }

            if (transactionCount > 0) {
                customerRevenues.add(new CompanyRevenueReportDTO.CustomerRevenue(
                        customer.getName(),
                        transactionCount,
                        customerTotal
                ));
                totalRevenue = totalRevenue.add(customerTotal);
            }
        }

        return new CompanyRevenueReportDTO(
                formattedStartDate,
                formattedEndDate,
                customerRevenues,
                totalRevenue
        );
    }


}
