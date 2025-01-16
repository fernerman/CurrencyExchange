package org.project.cursexchange.exception;

public class CurrencyCodeNotFoundInPath extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Коды валют пары отсутствуют в адресе";
    public CurrencyCodeNotFoundInPath(){
        super(DEFAULT_MESSAGE);
    }
    public CurrencyCodeNotFoundInPath(String message) {
        super(message);
    }
}
