package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	// =========================
	// 基本取得
	// =========================

	// クイズセットに属する問題を取得
	// → QuizAdminService：クイズセット削除時に使用
	List<Question> findByQuizSet(QuizSet quizSet);

	// クイズセットに属する問題を問題順で取得
	// → QuizService：クイズ出題時に使用
	List<Question> findByQuizSetOrderByQuestionOrder(QuizSet quizSet);


	// =========================
	// 問題一覧：絞り込み
	// =========================

	// カテゴリで絞り込み
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryId(Integer categoryId);

	// 章で絞り込み
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetId(Integer quizSetId);

	// カテゴリ＋章で絞り込み
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuizSetId(
			Integer categoryId,
			Integer quizSetId);

	// 問題文で検索
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuestionTextContainingIgnoreCase(String keyword);

	// カテゴリ＋問題文で検索
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuestionTextContainingIgnoreCase(
			Integer categoryId,
			String keyword);

	// 章＋問題文で検索
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetIdAndQuestionTextContainingIgnoreCase(
			Integer quizSetId,
			String keyword);

	// カテゴリ＋章＋問題文で検索
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuizSetIdAndQuestionTextContainingIgnoreCase(
			Integer categoryId,
			Integer quizSetId,
			String keyword);


	// =========================
	// 問題追加・削除時の並び順管理
	// =========================

	// クイズセット内の最後の問題番号を取得
	// → QuizAdminService：問題追加時に使用
	//    新しい問題を最後尾に追加するために使用
	@Query("SELECT COALESCE(MAX(q.questionOrder), 0) FROM Question q WHERE q.quizSet = :quizSet")
	Integer findMaxQuestionOrder(@Param("quizSet") QuizSet quizSet);

	// 問題削除後、後続の問題番号を1つ詰める
	// → QuizAdminService：問題削除時に使用
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


	// =========================
	// 問題一覧：並び順付き
	// =========================

	// カテゴリで絞り込み、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdOrderByQuestionOrderAsc(
			Integer categoryId);

	// 章で絞り込み、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetIdOrderByQuestionOrderAsc(
			Integer quizSetId);

	// カテゴリ＋章で絞り込み、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuizSetIdOrderByQuestionOrderAsc(
			Integer categoryId,
			Integer quizSetId);

	// 問題文で検索し、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			String keyword);

	// カテゴリ＋問題文で検索し、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer categoryId,
			String keyword);

	// 章＋問題文で検索し、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer quizSetId,
			String keyword);

	// カテゴリ＋章＋問題文で検索し、問題順で表示
	// → QuizAdminController：問題一覧画面で使用
	List<Question> findByQuizSetCategoryIdAndQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
			Integer categoryId,
			Integer quizSetId,
			String keyword);

	// 全問題を問題順で取得
	// → QuizAdminController：問題一覧画面で使用
	//    絞り込み条件がない場合に使用
	List<Question> findAllByOrderByQuestionOrderAsc();

}