package org.project.cursexchange.exception;

public class DataAccesException extends RuntimeException{
    private static final String DEFAULT_MESSAGE="Ошибка при выполнении запроса к базе данных";

    public DataAccesException() {
        super(DEFAULT_MESSAGE);
    }

    public DataAccesException(String message){
        super(message);
    }
}
