package com.klepek.datify.exception;

public class TextExtractionException extends Exception {
    public TextExtractionException(String message) {
        super(message);
    }

    public TextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}