package com.klepek.datify.dto;

public class GlobalAnswerResponse {

    private String answer;
    private String question;

    public GlobalAnswerResponse() {}

    public GlobalAnswerResponse(String answer, String question) {
        this.answer = answer;
        this.question = question;
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
}