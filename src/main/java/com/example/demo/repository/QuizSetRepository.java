package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Category;
import com.example.demo.entity.QuizSet;

public interface QuizSetRepository extends JpaRepository<QuizSet, Integer> {

	// =========================
	// クイズセット取得
	// =========================

	// 指定したカテゴリに属するクイズセットを取得
	// → 問題一覧画面などで、カテゴリによる絞り込みに使用
	List<QuizSet> findByCategoryId(Integer categoryId);

	// 指定したカテゴリに属するクイズセットを章順で取得
	// → クイズ出題画面で、章を正しい順番で表示するために使用
	List<QuizSet> findByCategoryOrderByQuizSetOrder(Category category);

	// すべてのクイズセットを章順で取得
	// → 管理画面のクイズセット一覧など、カテゴリを指定しない場合に使用
	List<QuizSet> findAllByOrderByQuizSetOrderAsc();


	// =========================
	// クイズセット並び順管理
	// =========================

	// 指定したカテゴリ内で、現在の最大の章順を取得
	// → クイズセット追加時に、最後尾の番号を設定するために使用
	@Query("SELECT COALESCE(MAX(q.quizSetOrder), 0) " +
	       "FROM QuizSet q WHERE q.category = :category")
	Integer findMaxQuizSetOrder(
			@Param("category") Category category);

	// ※章削除後の番号詰めはRepositoryではなくService側で処理する
	// → 削除した章より後ろのquizSetOrderを1つずつ減らす
	@Modifying
	@Query("""
		    UPDATE QuizSet q
		    SET q.quizSetOrder = q.quizSetOrder - 1
		    WHERE q.category = :category
		      AND q.quizSetOrder > :quizSetOrder
		""")
	void decrementQuizSetOrder(
			@Param("category") Category category,
			@Param("quizSetOrder") Integer quizSetOrder);
	
	List<QuizSet> findByCategoryIdOrderByQuizSetOrderAsc(Integer categoryId);
}