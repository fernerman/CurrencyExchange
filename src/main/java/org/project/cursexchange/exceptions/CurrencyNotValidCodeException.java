package org.project.cursexchange.exceptions;

public class CurrencyNotValidCodeException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Код валюты отсутствует в адресе";
    public CurrencyNotValidCodeException(){
        super(DEFAULT_MESSAGE);
    }
    public CurrencyNotValidCodeException(String message){
        super(message);
    }
}
