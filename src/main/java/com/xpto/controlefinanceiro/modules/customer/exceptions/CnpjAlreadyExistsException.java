package com.xpto.controlefinanceiro.modules.customer.exceptions;

public class CnpjAlreadyExistsException extends RuntimeException {
    public CnpjAlreadyExistsException(String message) {
        super(message);
    }
}
