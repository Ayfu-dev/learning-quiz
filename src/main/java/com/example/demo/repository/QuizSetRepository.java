package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Category;
import com.example.demo.entity.QuizSet;

public interface QuizSetRepository extends JpaRepository<QuizSet, Integer> {
	
	List<QuizSet> findByCategoryId(Integer categoryId);
	
	List<QuizSet> findByCategoryOrderByQuizSetOrder(Category category);
	
	@Query("SELECT COALESCE(MAX(q.quizSetOrder), 0) FROM QuizSet q WHERE q.category = :category")
	Integer findMaxQuizSetOrder(@Param("category") Category category);
}