package org.project.cursexchange.exception;

public class DataAccessException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Ошибка при выполнении запроса к базе данных";

    public DataAccessException() {
        super(DEFAULT_MESSAGE);
    }
}
