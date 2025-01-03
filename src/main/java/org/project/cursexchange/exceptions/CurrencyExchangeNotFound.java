package org.project.cursexchange.exceptions;

public class CurrencyExchangeNotFound extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Обменный курс для пары не найден";
    public CurrencyExchangeNotFound(){
        super(DEFAULT_MESSAGE);
    }
    public CurrencyExchangeNotFound(String message) {
        super(message);
    }
}
