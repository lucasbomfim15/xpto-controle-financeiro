package com.xpto.controlefinanceiro.modules.account.mappers;

import com.xpto.controlefinanceiro.modules.account.dtos.AccountRequestDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountResponseDTO;
import com.xpto.controlefinanceiro.modules.account.dtos.AccountUpdateDTO;
import com.xpto.controlefinanceiro.modules.account.model.Account;
import com.xpto.controlefinanceiro.modules.customer.model.Customer;

public class AccountMapper {

    public static Account toEntity(AccountRequestDTO dto, Customer customer) {
        return Account.builder()
                .bank(dto.bank())
                .agency(dto.agency())
                .number(dto.number())
                .balance(dto.balance())
                .customer(customer)
                .active(true)
                .build();
    }

    public static AccountResponseDTO toResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getBank(),
                account.getAgency(),
                account.getNumber(),
                account.getBalance(),
                account.getCustomer().getId(),
                account.isActive()
        );
    }

    public static void updateEntity(Account account, AccountUpdateDTO dto) {
        account.setBank(dto.bank());
        account.setAgency(dto.agency());
        account.setNumber(dto.number());
        account.setBalance(dto.balance());
    }
}
