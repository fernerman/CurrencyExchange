package org.project.cursexchange.exception;

public class CurrencyNotFound extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Currency is not found.";

    public CurrencyNotFound() {
        super(DEFAULT_MESSAGE);
    }

    public CurrencyNotFound(String message) {
        super(message);
    }
}