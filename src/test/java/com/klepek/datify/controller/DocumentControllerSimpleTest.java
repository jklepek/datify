package com.klepek.datify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klepek.datify.dto.QuestionRequest;
import com.klepek.datify.entity.Document;
import com.klepek.datify.exception.DocumentNotFoundException;
import com.klepek.datify.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllDocuments_ShouldReturnDocumentList() throws Exception {
        Document doc1 = createTestDocument(1L, "test1.txt");
        Document doc2 = createTestDocument(2L, "test2.txt");
        List<Document> documents = Arrays.asList(doc1, doc2);

        when(documentService.getAllDocuments()).thenReturn(documents);

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void askQuestion_ShouldReturn404ForNonExistentDocument() throws Exception {
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("What is this document about?");

        when(documentService.askQuestion(eq(999L), eq(request.getQuestion())))
                .thenThrow(new DocumentNotFoundException(999L));

        mockMvc.perform(post("/api/documents/999/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Document not found"));
    }

    private Document createTestDocument(Long id, String filename) {
        Document document = new Document();
        document.setId(id);
        document.setFilename(filename);
        document.setExtractedText("Test content for " + filename);
        document.setUploadedAt(LocalDateTime.now());
        return document;
    }
}