package org.project.cursexchange.exception;

public class ExchangeRateNotFound extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Exchange rate not found";
    public ExchangeRateNotFound() {
        super(DEFAULT_MESSAGE);
    }

}
