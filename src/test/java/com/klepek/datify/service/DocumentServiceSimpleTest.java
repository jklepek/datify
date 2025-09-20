package com.klepek.datify.service;

import com.klepek.datify.entity.Document;
import com.klepek.datify.exception.DocumentNotFoundException;
import com.klepek.datify.repository.DocumentRepository;
import com.klepek.datify.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceSimpleTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private DocumentVectorService vectorService;

    @Mock
    private GeminiService geminiService;

    @Mock
    private InvoiceExtractionService invoiceExtractionService;

    @InjectMocks
    private DefaultDocumentService documentService;

    private Document testDocument;

    @BeforeEach
    void setUp() {
        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setFilename("test.txt");
        testDocument.setExtractedText("Test document content");
        testDocument.setUploadedAt(LocalDateTime.now());
    }

    @Test
    void getAllDocuments_ShouldReturnAllDocuments() {
        List<Document> expectedDocuments = Arrays.asList(testDocument);
        when(documentRepository.findAll()).thenReturn(expectedDocuments);

        List<Document> result = documentService.getAllDocuments();

        assertEquals(expectedDocuments, result);
        verify(documentRepository).findAll();
    }

    @Test
    void getDocumentById_ShouldReturnDocument() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        Optional<Document> result = documentService.getDocumentById(1L);

        assertTrue(result.isPresent());
        assertEquals(testDocument, result.get());
        verify(documentRepository).findById(1L);
    }

    @Test
    void getDocumentById_ShouldReturnEmptyForNonExistentDocument() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Document> result = documentService.getDocumentById(999L);

        assertFalse(result.isPresent());
        verify(documentRepository).findById(999L);
    }

    @Test
    void askQuestion_ShouldThrowExceptionForNonExistentDocument() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
            documentService.askQuestion(999L, "Test question"));
    }

    @Test
    void uploadDocument_ShouldThrowExceptionForEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(IllegalArgumentException.class, () ->
            documentService.uploadDocument(emptyFile));
    }

    @Test
    void uploadDocument_ShouldThrowExceptionForUnsupportedFileType() {
        MockMultipartFile unsupportedFile = new MockMultipartFile("file", "test.doc", "application/msword", "content".getBytes());

        assertThrows(IllegalArgumentException.class, () ->
            documentService.uploadDocument(unsupportedFile));
    }
}