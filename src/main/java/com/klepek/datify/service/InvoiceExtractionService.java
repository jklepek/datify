package com.klepek.datify.service;

import com.klepek.datify.entity.Document;
import com.klepek.datify.entity.Invoice;

public interface InvoiceExtractionService {
    Invoice extractInvoiceData(Document document);
    boolean isInvoiceDocument(String filename, String extractedText);
}