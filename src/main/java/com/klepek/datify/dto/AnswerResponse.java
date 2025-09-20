package com.klepek.datify.dto;

public class AnswerResponse {

    private String answer;
    private String question;
    private Long documentId;
    private String documentFilename;

    public AnswerResponse() {}

    public AnswerResponse(String answer, String question, Long documentId, String documentFilename) {
        this.answer = answer;
        this.question = question;
        this.documentId = documentId;
        this.documentFilename = documentFilename;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
}