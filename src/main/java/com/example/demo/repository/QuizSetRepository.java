package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.QuizSet;

public interface QuizSetRepository extends JpaRepository<QuizSet, Integer> {
	
	List<QuizSet> findByCategoryId(Integer categoryId);

}