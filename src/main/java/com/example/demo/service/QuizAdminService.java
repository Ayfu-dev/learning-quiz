package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Category;
import com.example.demo.entity.Choice;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ChoiceRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizSetRepository;

@Service
public class QuizAdminService {

	private final QuizSetRepository quizSetRepository;
	private final QuestionRepository questionRepository;
	private final ChoiceRepository choiceRepository;
	private final CategoryRepository categoryRepository;

	public QuizAdminService(
			QuizSetRepository quizSetRepository,
			QuestionRepository questionRepository,
			ChoiceRepository choiceRepository,
			CategoryRepository categoryRepository) {

		this.quizSetRepository = quizSetRepository;
		this.questionRepository = questionRepository;
		this.choiceRepository = choiceRepository;
		this.categoryRepository = categoryRepository;
	}

	// 問題追加
	@Transactional
	public void addQuestion(
			Integer quizSetId,
			String questionText,
			String[] choiceTexts,
			Integer answerIndex,
			String explanation) {

		QuizSet quizSet = quizSetRepository.findById(quizSetId)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + quizSetId));

		// 新しい問題を最後尾に追加
		Integer maxOrder = questionRepository.findMaxQuestionOrder(quizSet);
		Integer questionOrder = maxOrder + 1;

		Question question = new Question(
				quizSet,
				questionText,
				answerIndex,
				explanation,
				questionOrder);

		question.setQuestionOrder(questionOrder);

		questionRepository.save(question);

		for (int i = 0; i < choiceTexts.length; i++) {

			Choice choice = new Choice(
					question,
					i,
					choiceTexts[i]);

			choiceRepository.save(choice);
		}
	}

	// 問題編集
	@Transactional
	public void editQuestion(
			Integer id,
			Integer quizSetId,
			String questionText,
			String[] choiceTexts,
			Integer answerIndex,
			String explanation) {

		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しない問題: " + id));

		QuizSet quizSet = quizSetRepository.findById(quizSetId)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + quizSetId));

		// 問題を更新
		question.update(
				quizSet,
				questionText,
				answerIndex,
				explanation);

		questionRepository.save(question);

		// 選択肢を更新
		List<Choice> choices = question.getChoices();

		for (int i = 0; i < choices.size(); i++) {
			Choice choice = choices.get(i);
			choice.update(i, choiceTexts[i]);
			choiceRepository.save(choice);
		}

	}

	@Transactional
	public void deleteQuestion(Integer id) {

		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しない問題: " + id));

		QuizSet quizSet = question.getQuizSet();
		Integer questionOrder = question.getQuestionOrder();

		// 紐づいている選択肢を削除
		List<Choice> choices = question.getChoices();

		for (Choice choice : choices) {
			choiceRepository.delete(choice);
		}

		// 問題を削除
		questionRepository.delete(question);

		// 後ろの問題の順番を1つ前に詰める
		questionRepository.decrementQuestionOrder(
				quizSet,
				questionOrder);
	}

	// クイズセット 章追加
	@Transactional
	public void addQuizSet(
			Integer categoryId,
			String title) {

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないカテゴリ: " + categoryId));

		QuizSet quizSet = new QuizSet(
				category,
				title);

		quizSetRepository.save(quizSet);
	}

	// 章編集
	@Transactional
	public void editQuizSet(
			Integer id,
			Integer categoryId,
			String title) {

		QuizSet quizSet = quizSetRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + id));

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないカテゴリ: " + categoryId));

		quizSet.update(
				category,
				title);

		quizSetRepository.save(quizSet);
	}

	@Transactional
	public void deleteQuizSet(Integer id) {

		QuizSet quizSet = quizSetRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + id));

		// クイズセットに属する問題を取得
		List<Question> questions = questionRepository.findByQuizSet(quizSet);
		// 問題と選択肢を削除
		for (Question question : questions) {

			List<Choice> choices = question.getChoices();

			for (Choice choice : choices) {
				choiceRepository.delete(choice);
			}

			questionRepository.delete(question);
		}

		// クイズセットを削除
		quizSetRepository.delete(quizSet);
	}

	@Transactional
	public void updateQuestionOrder(
			Integer quizSetId,
			List<Integer> questionIds,
			List<Integer> questionOrders) {

		QuizSet quizSet = quizSetRepository.findById(quizSetId)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + quizSetId));

		List<Question> questions = questionRepository.findByQuizSetOrderByQuestionOrder(quizSet);

		int questionCount = questions.size();

		// 件数と入力数が一致するか
		if (questionIds.size() != questionCount
				|| questionOrders.size() != questionCount) {

			throw new IllegalArgumentException(
					"問題の並び順を正しく入力してください。");
		}

		// 1～Nの範囲か確認
		boolean[] used = new boolean[questionCount + 1];

		for (Integer order : questionOrders) {

			if (order == null
					|| order < 1
					|| order > questionCount) {

				throw new IllegalArgumentException(
						"順番は1～" + questionCount + "の範囲で入力してください。");
			}

			// 重複チェック
			if (used[order]) {

				throw new IllegalArgumentException(
						"同じ順番が重複しています。");
			}

			used[order] = true;
		}

		// 問題の順番を更新
		for (int i = 0; i < questionIds.size(); i++) {

			Integer questionId = questionIds.get(i);
			Integer questionOrder = questionOrders.get(i);

			Question question = questionRepository.findById(questionId)
					.orElseThrow(() -> new IllegalArgumentException(
							"存在しない問題: " + questionId));

			// 別のクイズセットの問題を変更できないようにする
			if (!question.getQuizSet().getId().equals(quizSetId)) {
				throw new IllegalArgumentException(
						"指定された問題がクイズセットと一致しません。");
			}

			question.setQuestionOrder(questionOrder);
		}
	}
}