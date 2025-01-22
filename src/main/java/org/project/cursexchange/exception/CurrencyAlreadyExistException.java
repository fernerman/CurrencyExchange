package org.project.cursexchange.exception;

public class CurrencyAlreadyExistException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "A currency with this code already exists.";
    public CurrencyAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }
}
