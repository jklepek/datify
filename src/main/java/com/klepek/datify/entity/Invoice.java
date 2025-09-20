package com.klepek.datify.entity;

import com.klepek.datify.dto.InvoiceExtractionDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "vendor_address")
    private String vendorAddress;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "description")
    private String description;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "extracted_at")
    private LocalDateTime extractedAt;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "extraction_notes")
    private String extractionNotes;

    // Constructors
    public Invoice() {}

    public Invoice(Document document, String invoiceNumber, String vendorName,
                   LocalDate invoiceDate, LocalDate dueDate, BigDecimal totalAmount,
                   String currency) {
        this.document = document;
        this.invoiceNumber = invoiceNumber;
        this.vendorName = vendorName;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.extractedAt = LocalDateTime.now();
    }

    public static Invoice fromExtractionDto(Document document, InvoiceExtractionDto dto) {
        Invoice invoice = new Invoice();
        invoice.setDocument(document);
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setVendorName(dto.getVendorName());
        invoice.setVendorAddress(dto.getVendorAddress());
        invoice.setDescription(dto.getDescription());
        invoice.setPurchaseOrderNumber(dto.getPurchaseOrderNumber());
        invoice.setCurrency(dto.getCurrency());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setTotalAmount(dto.getTotalAmount());
        invoice.setTaxAmount(dto.getTaxAmount());
        invoice.setConfidenceScore(dto.getConfidenceScore());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setExtractedAt(LocalDateTime.now());

        String notes = String.format("Extracted from %s with confidence %.2f",
            document.getFilename(),
            dto.getConfidenceScore() != null ? dto.getConfidenceScore() : 0.0);
        invoice.setExtractionNotes(notes);

        return invoice;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
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
