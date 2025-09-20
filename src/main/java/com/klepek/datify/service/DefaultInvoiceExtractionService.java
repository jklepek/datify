package com.klepek.datify.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.klepek.datify.dto.InvoiceExtractionDto;
import com.klepek.datify.entity.Document;
import com.klepek.datify.entity.Invoice;
import com.klepek.datify.entity.InvoiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class DefaultInvoiceExtractionService implements InvoiceExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultInvoiceExtractionService.class);

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final JsonFactory jsonFactory;

    private static final String EXTRACTION_PROMPT = """
        You are an expert invoice data extraction system. Extract the following information from this document text in any language.

        Return the data as a JSON object with exactly these fields (use null if not found):
        {
            "invoiceNumber": "string",
            "vendorName": "string",
            "vendorAddress": "string",
            "invoiceDate": "YYYY-MM-DD or null",
            "dueDate": "YYYY-MM-DD or null",
            "totalAmount": "number as decimal, no currency symbols",
            "taxAmount": "number as decimal, no currency symbols or null",
            "currency": "3-letter currency code like USD, EUR, CZK",
            "description": "brief description of goods/services",
            "purchaseOrderNumber": "string or null",
            "confidenceScore": "number between 0.0 and 1.0"
        }

        Important guidelines:
        - This document may be in any language (English, Czech, German, etc.)
        - Extract dates in YYYY-MM-DD format only
        - Remove all currency symbols from amounts (extract numbers only)
        - Use standard currency codes (USD, EUR, GBP, CZK, etc.)
        - Confidence score should reflect how certain you are about the extraction
        - If information is clearly not present, use null
        - Be conservative with amounts - only extract if you're confident

        Document text to analyze:
        """;

    public DefaultInvoiceExtractionService(GeminiService geminiService) {
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.jsonFactory = new JsonFactory();
    }

    public Invoice extractInvoiceData(Document document) {
        logger.info("Starting invoice data extraction for document: {}", document.getFilename());

        try {
            String extractedText = document.getExtractedText();
            if (extractedText == null || extractedText.trim().isEmpty()) {
                logger.warn("No text content available for extraction from document: {}", document.getFilename());
                return createEmptyInvoice(document, "No text content available");
            }

            String prompt = EXTRACTION_PROMPT + "\n\n" + extractedText;
            String geminiResponse = geminiService.generateAnswer("Extract invoice data", prompt);

            logger.debug("Gemini extraction response: {}", geminiResponse);

            return parseGeminiResponse(document, geminiResponse);

        } catch (Exception e) {
            logger.error("Error extracting invoice data from document: {}", document.getFilename(), e);
            return createEmptyInvoice(document, "Extraction failed: " + e.getMessage());
        }
    }

    private Invoice parseGeminiResponse(Document document, String response) {
        try {
            // Try to find JSON in the response (Gemini might include extra text)
            String jsonResponse = extractJsonFromResponse(response);
            InvoiceExtractionDto dto = objectMapper.readValue(jsonResponse, InvoiceExtractionDto.class);

            Invoice invoice = Invoice.fromExtractionDto(document, dto);

            logger.info("Successfully extracted invoice data: vendor={}, amount={}, confidence={}",
                invoice.getVendorName(), invoice.getTotalAmount(), dto.getConfidenceScore());

            return invoice;

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse Gemini JSON response: {}", response, e);
            return createEmptyInvoice(document, "JSON parsing failed: " + e.getMessage());
        }
    }

    private String extractJsonFromResponse(String response) {
        String cleanedResponse = extractFromMarkdownCodeBlock(response);

        try (JsonParser parser = jsonFactory.createParser(cleanedResponse)) {
            while (parser.nextToken() != JsonToken.START_OBJECT && parser.currentToken() != null) {
                // Continue searching for JSON start
            }

            if (parser.currentToken() == JsonToken.START_OBJECT) {
                return objectMapper.writeValueAsString(objectMapper.readTree(parser));
            }

            return cleanedResponse.trim();
        } catch (Exception e) {
            logger.debug("Could not extract JSON using parser, falling back to cleaned response: {}", e.getMessage());
            return cleanedResponse.trim();
        }
    }

    private String extractFromMarkdownCodeBlock(String response) {
        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }



    private Invoice createEmptyInvoice(Document document, String errorNote) {
        Invoice invoice = new Invoice();
        invoice.setDocument(document);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setConfidenceScore(0.0);
        invoice.setExtractionNotes("Extraction failed: " + errorNote);
        return invoice;
    }

    private static final String INVOICE_DETECTION_PROMPT = """
        Analyze the following document text and determine if it represents an invoice, bill, or receipt in ANY language.

        Return a JSON object with this structure:
        {
            "isInvoice": true/false,
            "confidence": 0.0-1.0,
            "documentType": "invoice/bill/receipt/other",
            "language": "detected language (en/cs/de/etc)",
            "reason": "brief explanation of detection"
        }

        Consider these characteristics for invoice detection:
        - Contains vendor/supplier information
        - Has amounts or prices
        - Has dates (invoice date, due date, etc.)
        - Has invoice/bill number or reference
        - Lists goods or services provided
        - Contains payment terms or tax information

        Document text to analyze:
        """;

    public boolean isInvoiceDocument(String filename, String extractedText) {
        if (filename == null && extractedText == null) {
            return false;
        }

        // Quick filename check first
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.contains("invoice") || lowerFilename.contains("faktura") ||
                lowerFilename.contains("bill") || lowerFilename.contains("receipt") ||
                lowerFilename.contains("účet") || lowerFilename.contains("rechnung")) {
                return true;
            }
        }

        // Use Gemini for intelligent content analysis
        if (extractedText != null && !extractedText.trim().isEmpty()) {
            try {
                String textSample = extractedText.length() > 1500 ?
                    extractedText.substring(0, 1500) + "..." : extractedText;
                String prompt = INVOICE_DETECTION_PROMPT + "\n\n" + textSample;
                String response = geminiService.generateAnswer("Invoice detection", prompt);

                logger.debug("Invoice detection response: {}", response);

                // Parse JSON response
                String jsonResponse = extractJsonFromResponse(response);
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                boolean isInvoice = jsonNode.has("isInvoice") && jsonNode.get("isInvoice").asBoolean();
                double confidence = jsonNode.has("confidence") ? jsonNode.get("confidence").asDouble() : 0.0;

                logger.info("Invoice detection result: isInvoice={}, confidence={}", isInvoice, confidence);

                return isInvoice && confidence > 0.5;

            } catch (Exception e) {
                logger.warn("Failed to use Gemini for invoice detection, falling back to basic detection", e);
                // Fallback to basic keyword detection
                String lowerContent = extractedText.toLowerCase();
                return lowerContent.contains("invoice") || lowerContent.contains("faktura") ||
                       lowerContent.contains("bill") || lowerContent.contains("účet") ||
                       lowerContent.contains("rechnung") || lowerContent.contains("total") ||
                       lowerContent.contains("celkem") || lowerContent.contains("amount");
            }
        }

        return false;
    }
}
