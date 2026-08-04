package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.CategoryQuiz;

public interface QuizRepository {

    List<CategoryQuiz> findAll();

}