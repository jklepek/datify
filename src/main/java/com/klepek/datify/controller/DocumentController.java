package com.klepek.datify.controller;

import com.klepek.datify.dto.*;
import com.klepek.datify.entity.Document;
import com.klepek.datify.entity.Invoice;
import com.klepek.datify.entity.InvoiceStatus;
import com.klepek.datify.exception.DocumentNotFoundException;
import com.klepek.datify.exception.GeminiApiException;
import com.klepek.datify.exception.TextExtractionException;
import com.klepek.datify.service.DocumentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000"})
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException, TextExtractionException {
        logger.debug("File details - Size: {}, ContentType: {}", file.getSize(), file.getContentType());

        Document document = documentService.uploadDocument(file);
        logger.debug("Document uploaded successfully with ID: {}", document.getId());
        return ResponseEntity.ok(new DocumentResponse(document));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        logger.debug("Found {} documents", documents.size());

        List<DocumentResponse> responses = documents.stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());

        logger.debug("Successfully returning {} document responses", responses.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(document -> ResponseEntity.ok(new DocumentResponse(document)))
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    @PostMapping("/{id}/ask")
    public ResponseEntity<AnswerResponse> askQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) throws GeminiApiException {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        String answer = documentService.askQuestion(id, request.getQuestion());

        AnswerResponse response = new AnswerResponse(
                answer,
                request.getQuestion(),
                document.getId(),
                document.getFilename()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask")
    public ResponseEntity<GlobalAnswerResponse> askGlobalQuestion(@Valid @RequestBody QuestionRequest request) throws GeminiApiException {
        logger.debug("Received global question: {}", request.getQuestion());

        String answer = documentService.askGlobalQuestion(request.getQuestion());

        GlobalAnswerResponse response = new GlobalAnswerResponse(answer, request.getQuestion());
        return ResponseEntity.ok(response);
    }

    // Invoice endpoints
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {

        List<Invoice> invoices = documentService.getAllInvoices();
        logger.debug("Found {} invoices", invoices.size());

        List<InvoiceResponse> responses = invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/invoices/overdue")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        List<Invoice> overdueInvoices = documentService.getOverdueInvoices();
        logger.debug("Found {} overdue invoices", overdueInvoices.size());

        List<InvoiceResponse> responses = overdueInvoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/invoices/summary")
    public ResponseEntity<InvoiceSummaryResponse> getInvoiceSummary() {
        logger.info("Received request to get invoice summary");

        List<Invoice> allInvoices = documentService.getAllInvoices();
        List<Invoice> overdueInvoices = documentService.getOverdueInvoices();
        
        long totalCount = allInvoices.size();
        long overdueCount = overdueInvoices.size();

        BigDecimal totalAmount = allInvoices.stream()
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overdueAmount = overdueInvoices.stream()
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = allInvoices.stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoiceSummaryResponse summary = new InvoiceSummaryResponse(
                totalCount, overdueCount, totalAmount, overdueAmount, paidAmount);

        logger.debug("Successfully returning invoice summary: {} total, {} overdue", totalCount, overdueCount);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<InvoiceResponse> getInvoiceForDocument(@PathVariable Long id) {
        return documentService.getInvoiceByDocumentId(id)
                .map(invoice -> {
                    logger.info("Found invoice for document {}: {}", id, invoice.getInvoiceNumber());
                    return ResponseEntity.ok(new InvoiceResponse(invoice));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/invoices/vendor/{vendorName}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByVendor(@PathVariable String vendorName) {
        List<Invoice> invoices = documentService.getInvoicesByVendor(vendorName);
        logger.debug("Found {} invoices for vendor: {}", invoices.size(), vendorName);

        List<InvoiceResponse> responses = invoices.stream()
                .map(InvoiceResponse::new)
                .collect(Collectors.toList());

        logger.debug("Successfully returning {} invoice responses for vendor: {}", responses.size(), vendorName);
        return ResponseEntity.ok(responses);
    }
}
