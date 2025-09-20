package com.klepek.datify.service;

import com.klepek.datify.exception.GeminiApiException;

public interface GeminiService {
    String generateAnswer(String question, String context) throws GeminiApiException;
}