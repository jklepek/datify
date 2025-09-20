package com.klepek.datify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionRequest {

    @NotBlank(message = "Question is required")
    @Size(min = 3, max = 1000, message = "Question must be between 3 and 1000 characters")
    private String question;

    public QuestionRequest() {}

    public QuestionRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}