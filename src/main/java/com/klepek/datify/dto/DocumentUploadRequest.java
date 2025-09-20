package com.klepek.datify.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

public class DocumentUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

    public DocumentUploadRequest() {}

    public DocumentUploadRequest(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}