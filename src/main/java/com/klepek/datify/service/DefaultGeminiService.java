package com.klepek.datify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class DefaultGeminiService implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGeminiService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public DefaultGeminiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        logger.info("Initializing GeminiService");
        try {
            this.webClient = webClientBuilder
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            this.objectMapper = objectMapper;
            logger.info("GeminiService initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing GeminiService", e);
            throw e;
        }
    }

    public String generateAnswer(String question, String context) {
        logger.debug("Generating answer for question of length: {}", question.length());

        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("Gemini API key is not properly configured");
            throw new RuntimeException("Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable.");
        }

        String prompt = String.format(
                "Na základě následujícího kontextu z dokumentu odpovězte na otázku v češtině. " +
                "Pokud odpověď není v kontextu dostupná, řekněte 'Na základě poskytnutého dokumentu nemohu odpovědět na tuto otázku.'\n\n" +
                "Kontext: %s\n\n" +
                "Otázka: %s\n\n" +
                "Odpověď:",
                context, question
        );

        var requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "maxOutputTokens", 500
                )
        );

        try {
            logger.debug("Sending request to Gemini API");
            String response = webClient.post()
                    .uri("/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.debug("Received response from Gemini API");
            JsonNode jsonNode = objectMapper.readTree(response);

            // Check if there are candidates
            if (!jsonNode.has("candidates") || jsonNode.get("candidates").isEmpty()) {
                logger.warn("No candidates in Gemini response");
                return "Omlouváme se, nepodařilo se vygenerovat odpověď.";
            }

            JsonNode candidate = jsonNode.get("candidates").get(0);
            if (!candidate.has("content") || !candidate.get("content").has("parts")) {
                logger.warn("Invalid response structure from Gemini");
                return "Omlouváme se, nepodařilo se vygenerovat odpověď.";
            }

            String answer = candidate.get("content").get("parts").get(0).get("text").asText();
            logger.debug("Successfully generated answer of length: {}", answer.length());
            return answer.trim();

        } catch (Exception e) {
            logger.error("Failed to generate answer with Gemini", e);
            throw new RuntimeException("Failed to generate answer: " + e.getMessage(), e);
        }
    }
}