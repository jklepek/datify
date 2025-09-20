package com.klepek.datify.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("errorCode", "INTERNAL_ERROR");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("Bad request: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad request");
        errorResponse.put("errorCode", "INVALID_INPUT");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentNotFound(DocumentNotFoundException e) {
        logger.warn("Document not found: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Document not found");
        errorResponse.put("errorCode", "DOCUMENT_NOT_FOUND");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<Map<String, Object>> handleGeminiApiException(GeminiApiException e) {
        logger.error("Gemini API error: {}", e.getMessage(), e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "AI service unavailable");
        errorResponse.put("errorCode", "AI_SERVICE_ERROR");
        errorResponse.put("message", "AI service is currently unavailable. Please try again later.");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(java.io.IOException e) {
        logger.error("IO error: {}", e.getMessage(), e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "File processing error");
        errorResponse.put("message", "Failed to process file: " + e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(TextExtractionException.class)
    public ResponseEntity<Map<String, Object>> handleTextExtractionException(TextExtractionException e) {
        logger.error("Text extraction error occurred", e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Text extraction failed");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        logger.warn("Validation error: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Validation failed");
        errorResponse.put("errorCode", "VALIDATION_ERROR");

        // Extract specific validation errors
        StringBuilder detailsBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (detailsBuilder.length() > 0) detailsBuilder.append("; ");
            detailsBuilder.append(error.getField()).append(": ").append(error.getDefaultMessage());
        });

        errorResponse.put("message", detailsBuilder.length() > 0 ? detailsBuilder.toString() : "Invalid input data");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("File upload size exceeded: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "File too large");
        errorResponse.put("errorCode", "FILE_SIZE_EXCEEDED");
        errorResponse.put("message", "File size exceeds maximum allowed size of 10MB");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}