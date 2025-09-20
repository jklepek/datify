package com.klepek.datify.dto;

import com.klepek.datify.entity.Invoice;
import com.klepek.datify.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private Long documentId;
    private String documentFilename;
    private String invoiceNumber;
    private String vendorName;
    private String vendorAddress;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private String currency;
    private InvoiceStatus status;
    private String description;
    private String purchaseOrderNumber;
    private LocalDateTime extractedAt;
    private Double confidenceScore;
    private String extractionNotes;

    // Constructor
    public InvoiceResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.documentId = invoice.getDocument().getId();
        this.documentFilename = invoice.getDocument().getFilename();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.vendorName = invoice.getVendorName();
        this.vendorAddress = invoice.getVendorAddress();
        this.invoiceDate = invoice.getInvoiceDate();
        this.dueDate = invoice.getDueDate();
        this.totalAmount = invoice.getTotalAmount();
        this.taxAmount = invoice.getTaxAmount();
        this.currency = invoice.getCurrency();
        this.status = invoice.getStatus();
        this.description = invoice.getDescription();
        this.purchaseOrderNumber = invoice.getPurchaseOrderNumber();
        this.extractedAt = invoice.getExtractedAt();
        this.confidenceScore = invoice.getConfidenceScore();
        this.extractionNotes = invoice.getExtractionNotes();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentFilename() {
        return documentFilename;
    }

    public void setDocumentFilename(String documentFilename) {
        this.documentFilename = documentFilename;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public LocalDateTime getExtractedAt() {
        return extractedAt;
    }

    public void setExtractedAt(LocalDateTime extractedAt) {
        this.extractedAt = extractedAt;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getExtractionNotes() {
        return extractionNotes;
    }

    public void setExtractionNotes(String extractionNotes) {
        this.extractionNotes = extractionNotes;
    }
}