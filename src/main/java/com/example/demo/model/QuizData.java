package com.example.demo.model;

import java.util.List;

public class QuizData {

    private String category;
    private List<QuizSet> quizSets;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<QuizSet> getQuizSets() {
        return quizSets;
    }

    public void setQuizSets(List<QuizSet> quizSets) {
        this.quizSets = quizSets;
    }
}