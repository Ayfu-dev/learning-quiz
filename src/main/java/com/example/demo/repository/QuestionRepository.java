package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	List<Question> findByQuizSet(QuizSet quizSet);

	List<Question> findByQuizSetCategoryId(Integer categoryId);

	List<Question> findByQuizSetId(Integer quizSetId);

	List<Question> findByQuizSetCategoryIdAndQuizSetId(
			Integer categoryId,
			Integer quizSetId);

	List<Question> findByQuestionTextContainingIgnoreCase(String keyword);

	List<Question> findByQuizSetCategoryIdAndQuestionTextContainingIgnoreCase(
			Integer categoryId,
			String keyword);

	List<Question> findByQuizSetIdAndQuestionTextContainingIgnoreCase(
			Integer quizSetId,
			String keyword);

	List<Question> findByQuizSetCategoryIdAndQuizSetIdAndQuestionTextContainingIgnoreCase(
			Integer categoryId,
			Integer quizSetId,
			String keyword);
}