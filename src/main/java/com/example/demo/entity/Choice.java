package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "choice_index", nullable = false)
    private Integer choiceIndex;

    @Column(name = "choice_text", nullable = false, length = 300)
    private String choiceText;

    protected Choice() {}

    public Choice(Question question, Integer choiceIndex, String choiceText) {
        this.question = question;
        this.choiceIndex = choiceIndex;
        this.choiceText = choiceText;
    }
    
    public void update(
            Integer choiceIndex,
            String choiceText) {

        this.choiceIndex = choiceIndex;
        this.choiceText = choiceText;
    }

    public Integer getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public Integer getChoiceIndex() {
        return choiceIndex;
    }

    public String getChoiceText() {
        return choiceText;
    }
}
