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

		Question question = new Question(
				quizSet,
				questionText,
				answerIndex,
				explanation);

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

		// 紐づいている選択肢を削除
		List<Choice> choices = question.getChoices();

		for (Choice choice : choices) {
			choiceRepository.delete(choice);
		}

		// 問題を削除
		questionRepository.delete(question);
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
	            .orElseThrow(() ->
	                    new IllegalArgumentException(
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
	
}