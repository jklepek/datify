package com.klepek.datify.exception;

public class GeminiApiException extends Exception {
    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}