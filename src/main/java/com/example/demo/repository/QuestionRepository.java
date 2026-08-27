package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	List<Question> findByQuizSet(QuizSet quizSet);

	List<Question> findByQuizSetOrderByQuestionOrder(QuizSet quizSet);

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

	//最後尾を取得するために追加
	@Query("SELECT COALESCE(MAX(q.questionOrder), 0) FROM Question q WHERE q.quizSet = :quizSet")
	Integer findMaxQuestionOrder(@Param("quizSet") QuizSet quizSet);

	//字詰め目的
	@Modifying
	@Query("""
			    UPDATE Question q
			    SET q.questionOrder = q.questionOrder - 1
			    WHERE q.quizSet = :quizSet
			      AND q.questionOrder > :questionOrder
			""")
	void decrementQuestionOrder(
			@Param("quizSet") QuizSet quizSet,
			@Param("questionOrder") Integer questionOrder);

	List<Question> findByQuizSetCategoryIdOrderByQuestionOrderAsc(
			Integer categoryId);

	List<Question> findByQuizSetIdOrderByQuestionOrderAsc(
			Integer quizSetId);

	List<Question> findByQuizSetCategoryIdAndQuizSetIdOrderByQuestionOrderAsc(
			Integer categoryId,
			Integer quizSetId);

	List<Question> findByQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			String keyword);

	List<Question> findByQuizSetCategoryIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer categoryId,
			String keyword);

	List<Question> findByQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer quizSetId,
			String keyword);

	List<Question> findByQuizSetCategoryIdAndQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer categoryId,
			Integer quizSetId,
			String keyword);

	List<Question> findAllByOrderByQuestionOrderAsc();

}