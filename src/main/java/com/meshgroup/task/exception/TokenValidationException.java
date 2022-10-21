package com.meshgroup.task.exception;

public class TokenValidationException extends RuntimeException {

    public TokenValidationException() {
    }

    public TokenValidationException(String message) {
        super(message);
    }

}
