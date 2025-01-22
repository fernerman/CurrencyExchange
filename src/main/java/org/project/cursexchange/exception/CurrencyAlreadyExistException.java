package org.project.cursexchange.exception;

public class CurrencyExistException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "A currency with this code already exists.";

    public CurrencyExistException() {
        super(DEFAULT_MESSAGE);
    }

}
