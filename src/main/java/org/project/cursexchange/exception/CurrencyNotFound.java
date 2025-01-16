package org.project.cursexchange.exception;

public class CurrencyNotFound extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Валюта не найдена";

    public CurrencyNotFound() {
        super(DEFAULT_MESSAGE);
    }

    public CurrencyNotFound(String message) {
        super(message);
    }
}
