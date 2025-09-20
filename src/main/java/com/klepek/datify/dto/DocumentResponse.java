package com.klepek.datify.dto;

import com.klepek.datify.entity.Document;
import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String filename;
    private String contentType;
    private LocalDateTime uploadedAt;
    private int textLength;

    public DocumentResponse() {}

    public DocumentResponse(Document document) {
        this.id = document.getId();
        this.filename = document.getFilename();
        this.contentType = document.getContentType();
        this.uploadedAt = document.getUploadedAt();
        this.textLength = document.getExtractedText() != null ? document.getExtractedText().length() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }
}