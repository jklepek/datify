package com.klepek.datify.service;

import com.klepek.datify.entity.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefaultDocumentVectorService implements DocumentVectorService {

    private final VectorStore vectorStore;

    public DefaultDocumentVectorService( VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeDocument(Document document) {
        org.springframework.ai.document.Document aiDocument = new org.springframework.ai.document.Document(
            document.getExtractedText(),
            createDocumentMetadata(document)
        );

        vectorStore.add(List.of(aiDocument));
    }

    public List<org.springframework.ai.document.Document> findSimilarContent(String query, int topK, double threshold) {
        return vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(threshold)
                .build()
        );
    }

    private Map<String, Object> createDocumentMetadata(Document document) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("documentId", document.getId());
        metadata.put("filename", document.getFilename());
        metadata.put("contentType", document.getContentType());
        metadata.put("uploadedAt", document.getUploadedAt());
        return metadata;
    }
}
