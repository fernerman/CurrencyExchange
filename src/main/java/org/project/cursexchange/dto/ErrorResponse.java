package org.project.cursexchange.util;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse sendError(Exception exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
