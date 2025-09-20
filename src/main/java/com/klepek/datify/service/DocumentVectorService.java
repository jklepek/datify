package com.klepek.datify.service;

import com.klepek.datify.entity.Document;

import java.util.List;

public interface DocumentVectorService {
    void storeDocument(Document document);
    List<org.springframework.ai.document.Document> findSimilarContent(String query, int topK, double threshold);
}