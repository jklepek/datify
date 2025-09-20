package com.klepek.datify.service;

import com.klepek.datify.entity.Document;
import com.klepek.datify.entity.Invoice;
import com.klepek.datify.exception.DocumentNotFoundException;
import com.klepek.datify.exception.GeminiApiException;
import com.klepek.datify.exception.TextExtractionException;
import com.klepek.datify.repository.DocumentRepository;
import com.klepek.datify.repository.InvoiceRepository;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DefaultDocumentService implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentService.class);

    private final DocumentRepository documentRepository;
    private final InvoiceRepository invoiceRepository;
    private final GeminiService geminiService;
    private final DocumentVectorService vectorService;
    private final InvoiceExtractionService invoiceExtractionService;
    private final Tika tika;

    public DefaultDocumentService(DocumentRepository documentRepository,
                          InvoiceRepository invoiceRepository,
                          GeminiService geminiService,
                          DocumentVectorService vectorService,
                          InvoiceExtractionService invoiceExtractionService) {
        this.documentRepository = documentRepository;
        this.invoiceRepository = invoiceRepository;
        this.geminiService = geminiService;
        this.vectorService = vectorService;
        this.invoiceExtractionService = invoiceExtractionService;
        this.tika = new Tika();
    }

    public Document uploadDocument(MultipartFile file) throws IOException, TextExtractionException {
        logger.info("Starting document upload for file: {}", file.getOriginalFilename());

        try {
            validateFile(file);
            logger.debug("File validation passed");

            String extractedText = extractText(file);
            logger.debug("Text extraction completed, length: {}", extractedText.length());

            Document document = new Document(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    extractedText
            );

            Document savedDocument = documentRepository.save(document);
            logger.debug("Document saved with ID: {}", savedDocument.getId());

            logger.debug("Storing document in vector database...");
            vectorService.storeDocument(savedDocument);
            logger.debug("Document successfully stored in vector database with chunking");

            processInvoiceIfApplicable(savedDocument, extractedText);

            logger.info("Document upload completed successfully for file: {}", file.getOriginalFilename());
            return savedDocument;
        } catch (Exception e) {
            logger.error("Error uploading document: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }

    public List<Document> getAllDocuments() {
        logger.info("Getting all documents from repository");
        try {
            List<Document> documents = documentRepository.findAll();
            logger.info("Retrieved {} documents from database", documents.size());
            return documents;
        } catch (Exception e) {
            logger.error("Error retrieving documents from database", e);
            throw e;
        }
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public String askQuestion(Long documentId, String question) throws GeminiApiException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        String relevantContext = findRelevantContext(question, document);
        return geminiService.generateAnswer(question, relevantContext);
    }

    public String askGlobalQuestion(String question) throws GeminiApiException {
        logger.info("Processing global question across all documents: {}", question);
        String relevantContext = findGlobalRelevantContext(question);
        return geminiService.generateAnswer(question, relevantContext);
    }

    private void validateFile(MultipartFile file) {
        String lowerFilename = getFilename(file);
        if (!lowerFilename.endsWith(".pdf") && !lowerFilename.endsWith(".txt")) {
            throw new IllegalArgumentException("Only PDF and TXT files are supported");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("application/pdf") && !contentType.equals("text/plain"))) {
            throw new IllegalArgumentException("Invalid content type. Only PDF and TXT files are supported");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        if (file.getSize() == 0) {
            throw new IllegalArgumentException("File cannot be empty");
        }
    }

    private static String getFilename(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename is required");
        }

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: contains path traversal characters");
        }
        return filename.toLowerCase();
    }

    private String extractText(MultipartFile file) throws TextExtractionException {
        try (var inputStream = file.getInputStream()) {
            String text = tika.parseToString(inputStream);
            if (text == null || text.trim().isEmpty()) {
                throw new TextExtractionException("No text content found in the file");
            }
            return text.trim();
        } catch (Exception e) {
            throw new TextExtractionException("Failed to extract text from file: " + e.getMessage(), e);
        }
    }

    private String findRelevantContext(String question, Document document) {
        logger.debug("Performing semantic search for question: {}", question);
        List<org.springframework.ai.document.Document> similarChunks =
            vectorService.findSimilarContent(question, 5, 0.3);

        StringBuilder contextBuilder = new StringBuilder();
        for (org.springframework.ai.document.Document chunk : similarChunks) {
            Object docId = chunk.getMetadata().get("documentId");
            if (docId != null && docId.toString().equals(document.getId().toString())) {
                contextBuilder.append(chunk.getFormattedContent()).append("\n\n");
            }
        }

        String semanticContext = contextBuilder.toString().trim();

        if (semanticContext.length() < 100) {
            logger.debug("Insufficient semantic search results, using full document text");
            String documentText = document.getExtractedText();
            return documentText.length() <= 2000 ? documentText : documentText.substring(0, 2000);
        }

        logger.debug("Using semantic search context, length: {} characters", semanticContext.length());
        return semanticContext.length() <= 2000 ? semanticContext : semanticContext.substring(0, 2000);
    }

    private String findGlobalRelevantContext(String question) {
        logger.debug("Performing global semantic search for question: {}", question);
        List<org.springframework.ai.document.Document> similarChunks =
            vectorService.findSimilarContent(question, 10, 0.3);

        if (similarChunks.isEmpty()) {
            logger.debug("No semantic search results found, trying lower threshold");
            similarChunks = vectorService.findSimilarContent(question, 10, 0.1);
        }

        StringBuilder contextBuilder = new StringBuilder();
        Map<String, String> sourceDocuments = new HashMap<>();

        for (org.springframework.ai.document.Document chunk : similarChunks) {
            Object docId = chunk.getMetadata().get("documentId");
            Object filename = chunk.getMetadata().get("filename");

            if (docId != null && filename != null) {
                String docInfo = String.format("[Source: %s (ID: %s)]", filename, docId);
                if (!sourceDocuments.containsKey(docId.toString())) {
                    sourceDocuments.put(docId.toString(), filename.toString());
                }
                contextBuilder.append(docInfo).append("\n");
                contextBuilder.append(chunk.getFormattedContent()).append("\n\n");
            }
        }

        String globalContext = contextBuilder.toString().trim();

        if (globalContext.length() < 100) {
            logger.debug("Insufficient semantic search results, returning empty context");
            return "No relevant information found in the document collection for this question.";
        }

        logger.info("Found relevant content from {} documents", sourceDocuments.size());
        logger.debug("Using global semantic search context, length: {} characters", globalContext.length());

        return globalContext.length() <= 4000 ? globalContext : globalContext.substring(0, 4000);
    }

    private void processInvoiceIfApplicable(Document document, String extractedText) {
        try {
            if (invoiceExtractionService.isInvoiceDocument(document.getFilename(), extractedText)) {
                logger.info("Document appears to be an invoice, extracting structured data: {}", document.getFilename());

                Invoice invoice = invoiceExtractionService.extractInvoiceData(document);
                Invoice savedInvoice = invoiceRepository.save(invoice);

                logger.info("Invoice data extracted successfully: vendor={}, amount={}, confidence={}",
                    savedInvoice.getVendorName(),
                    savedInvoice.getTotalAmount(),
                    savedInvoice.getConfidenceScore());
            } else {
                logger.debug("Document does not appear to be an invoice: {}", document.getFilename());
            }
        } catch (Exception e) {
            logger.error("Error processing invoice data for document: {}", document.getFilename(), e);
            // Don't fail the upload if invoice extraction fails
        }
    }

    // Invoice-specific methods
    public Optional<Invoice> getInvoiceByDocumentId(Long documentId) {
        return invoiceRepository.findByDocumentId(documentId);
    }

    public List<Invoice> getAllInvoices() {
        logger.info("Getting all invoices from repository");
        return invoiceRepository.findAllOrderByDueDateAsc();
    }

    public List<Invoice> getOverdueInvoices() {
        logger.info("Getting overdue invoices");
        return invoiceRepository.findOverdueInvoices(java.time.LocalDate.now());
    }

    public List<Invoice> getInvoicesByVendor(String vendorName) {
        logger.info("Getting invoices for vendor: {}", vendorName);
        return invoiceRepository.findByVendorNameContainingIgnoreCase(vendorName);
    }
}
