package com.xpto.controlefinanceiro.modules.account.exceptions;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
