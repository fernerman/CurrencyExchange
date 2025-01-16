package org.project.cursexchange.exception;

public class CurrencyExistException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Валюта с таким кодом уже существует";
    public CurrencyExistException(){
        super(DEFAULT_MESSAGE);
    }
    public CurrencyExistException(String message) {
        super(message);
    }
}
