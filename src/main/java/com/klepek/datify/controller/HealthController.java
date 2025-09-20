package com.klepek.datify.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000"})
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        logger.info("Health check requested");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());

        boolean geminiConfigured = apiKey != null && !apiKey.trim().isEmpty();
        health.put("gemini_configured", geminiConfigured);

        if (!geminiConfigured) {
            logger.warn("Gemini API key is not properly configured");
        }

        return ResponseEntity.ok(health);
    }
}