package org.project.cursexchange.models;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public static ErrorResponse sendError(Exception exception){
        return new ErrorResponse(exception.getMessage());
    }
}
