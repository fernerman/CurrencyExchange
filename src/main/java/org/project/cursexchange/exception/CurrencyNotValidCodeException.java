package org.project.cursexchange.exception;

public class CurrencyNotValidCodeException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Код валюты отсутствует в адресе";

    public CurrencyNotValidCodeException() {
        super(DEFAULT_MESSAGE);
    }
}
