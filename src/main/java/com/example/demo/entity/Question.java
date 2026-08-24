package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id", nullable = false)
    private QuizSet quizSet;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Column(name = "answer_index", nullable = false)
    private Integer answerIndex;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<Choice> choices;

    protected Question() {}

    public Question(QuizSet quizSet, String questionText, Integer answerIndex, String explanation) {
        this.quizSet = quizSet;
        this.questionText = questionText;
        this.answerIndex = answerIndex;
        this.explanation = explanation;
    }
    
    public void update(
            QuizSet quizSet,
            String questionText,
            Integer answerIndex,
            String explanation) {

        this.quizSet = quizSet;
        this.questionText = questionText;
        this.answerIndex = answerIndex;
        this.explanation = explanation;
    }

    public Integer getId() {
        return id;
    }

    public QuizSet getQuizSet() {
        return quizSet;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Integer getAnswerIndex() {
        return answerIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<Choice> getChoices() {
        return List.copyOf(choices);
    }
}
