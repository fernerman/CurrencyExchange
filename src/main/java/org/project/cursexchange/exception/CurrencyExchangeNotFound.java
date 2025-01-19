package org.project.cursexchange.exception;

public class CurrencyExchangeNotFound extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Обменный курс для пары не найден";

    public CurrencyExchangeNotFound() {
        super(DEFAULT_MESSAGE);
    }

}
