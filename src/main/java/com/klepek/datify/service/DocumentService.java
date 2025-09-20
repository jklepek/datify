package com.klepek.datify.service;

import com.klepek.datify.entity.Document;
import com.klepek.datify.entity.Invoice;
import com.klepek.datify.exception.GeminiApiException;
import com.klepek.datify.exception.TextExtractionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document uploadDocument(MultipartFile file) throws IOException, TextExtractionException;
    List<Document> getAllDocuments();
    Optional<Document> getDocumentById(Long id);
    String askQuestion(Long documentId, String question) throws GeminiApiException;
    String askGlobalQuestion(String question) throws GeminiApiException;
    Optional<Invoice> getInvoiceByDocumentId(Long documentId);
    List<Invoice> getAllInvoices();
    List<Invoice> getOverdueInvoices();
    List<Invoice> getInvoicesByVendor(String vendorName);
}