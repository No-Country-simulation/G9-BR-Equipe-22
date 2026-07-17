package com.techtagger.backend.exception;

public class MLServiceUnavailableException extends RuntimeException {
    public MLServiceUnavailableException(String message) {
        super(message);
    }
    public MLServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}