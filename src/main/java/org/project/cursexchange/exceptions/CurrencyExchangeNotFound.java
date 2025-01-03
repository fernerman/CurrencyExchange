package org.project.cursexchange.exceptions;

public class ExchangeCurrencyNotFound extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Обменный курс для пары не найден";
    public ExchangeCurrencyNotFound(){
        super(DEFAULT_MESSAGE);
    }
    public ExchangeCurrencyNotFound(String message) {
        super(message);
    }
}
