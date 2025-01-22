package org.project.cursexchange.exception;

public class CurrencyNotValidCodeException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Currency`s code is not valid.";
    public CurrencyNotValidCodeException() {
        super(DEFAULT_MESSAGE);
    }
}
