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
@Table(name = "quiz_set")
public class QuizSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 20)
    private String title;

    @OneToMany(mappedBy = "quizSet", fetch = FetchType.LAZY)
    private List<Question> questions;
    
    @Column(name = "quiz_set_order", nullable = false)
    private Integer quizSetOrder;

    public void updateOrder(Integer quizSetOrder) {
        this.quizSetOrder = quizSetOrder;
    }
    
    protected QuizSet() {}

    public QuizSet(Category category, String title, Integer quizSetOrder) {
        this.category = category;
        this.title = title;
        this.quizSetOrder = quizSetOrder;
    }
    
    public void update(
            Category category,
            String title) {

        this.category = category;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return List.copyOf(questions);
    }
    
    public Integer getQuizSetOrder() {
        return quizSetOrder;
    }
    
    public Integer setQuizSetOrder() {
    	return quizSetOrder;
    }
}
