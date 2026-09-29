package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ChoiceRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizSetRepository;

@Service
public class QuizService {

	private final CategoryRepository categoryRepository;
	private final QuizSetRepository quizSetRepository;
	private final QuestionRepository questionRepository;
	private final ChoiceRepository choiceRepository;

	public QuizService(
			CategoryRepository categoryRepository,
			QuizSetRepository quizSetRepository,
			QuestionRepository questionRepository,
			ChoiceRepository choiceRepository) {

		this.categoryRepository = categoryRepository;
		this.quizSetRepository = quizSetRepository;
		this.questionRepository = questionRepository;
		this.choiceRepository = choiceRepository;
	}

	public List<com.example.demo.model.QuizSet> getQuizSets(String categoryName) {

		Category category = categoryRepository.findAll()
				.stream()
				.filter(c -> c.getName().equalsIgnoreCase(categoryName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないカテゴリ: " + categoryName));

		return category.getQuizSets()
				.stream()
				.sorted(Comparator.comparing(QuizSet::getQuizSetOrder))
				.map(this::toModelQuizSet)
				.toList();
	}

	public List<com.example.demo.model.Question> getQuestions(
			String categoryName,
			int setIndex) {

		return getQuizSets(categoryName)
				.get(setIndex)
				.getQuestions();
	}

	public boolean checkAnswer(
			String categoryName,
			int setIndex,
			int questionIndex,
			int userAnswer) {

		return getQuestions(categoryName, setIndex)
				.get(questionIndex)
				.getAnswerIndex() == userAnswer;
	}

	public List<com.example.demo.model.Question> createComprehensiveQuestions(
	        String categoryName,
	        List<Integer> selectedSetIndexes,
	        int questionCount,
	        List<Integer> questionSetIndexes) {

	    List<com.example.demo.model.QuizSet> quizSets =
	            getQuizSets(categoryName);

	    List<com.example.demo.model.Question> pool =
	            new ArrayList<>();

	    List<Integer> setIndexPool =
	            new ArrayList<>();

	    for (Integer setIndex : selectedSetIndexes) {

	        if (setIndex < 0 || setIndex >= quizSets.size()) {
	            continue;
	        }

	        var quizSet = quizSets.get(setIndex);

	        for (var question : quizSet.getQuestions()) {

	            pool.add(question);
	            setIndexPool.add(setIndex);
	        }
	    }

	    if (pool.isEmpty()) {
	        throw new IllegalArgumentException(
	                "出題できる問題がありません");
	    }

	    if (questionCount > pool.size()) {
	        throw new IllegalArgumentException(
	                "出題数が問題数を超えています");
	    }

	    // 問題とQuizSet番号を同じ順番でシャッフル
	    List<Integer> indexes = new ArrayList<>();

	    for (int i = 0; i < pool.size(); i++) {
	        indexes.add(i);
	    }

	    Collections.shuffle(indexes);

	    List<com.example.demo.model.Question> questions =
	            new ArrayList<>();

	    questionSetIndexes.clear();

	    for (int i = 0; i < questionCount; i++) {

	        int index = indexes.get(i);

	        questions.add(pool.get(index));
	        questionSetIndexes.add(setIndexPool.get(index));
	    }

	    return questions;
	}
	
	public List<Integer> createComprehensiveQuestionSetIndexes(
	        String categoryName,
	        List<Integer> selectedSetIndexes,
	        int questionCount) {

	    List<com.example.demo.model.QuizSet> quizSets =
	            getQuizSets(categoryName);

	    List<Integer> setIndexPool = new ArrayList<>();

	    for (Integer setIndex : selectedSetIndexes) {

	        if (setIndex < 0 || setIndex >= quizSets.size()) {
	            continue;
	        }

	        int questionSize =
	                quizSets.get(setIndex).getQuestions().size();

	        for (int i = 0; i < questionSize; i++) {
	            setIndexPool.add(setIndex);
	        }
	    }

	    Collections.shuffle(setIndexPool);

	    return new ArrayList<>(
	            setIndexPool.subList(0, questionCount));
	}

	private com.example.demo.model.QuizSet toModelQuizSet(
			QuizSet entity) {

		List<com.example.demo.model.Question> questions = entity.getQuestions()
				.stream()
				.sorted(Comparator.comparing(Question::getQuestionOrder))
				.map(this::toModelQuestion)
				.toList();

		return new com.example.demo.model.QuizSet(
				entity.getTitle(),
				questions);
	}

	private com.example.demo.model.Question toModelQuestion(
			Question entity) {

		List<String> choices = entity.getChoices()
				.stream()
				.map(choice -> choice.getChoiceText())
				.toList();

		return new com.example.demo.model.Question(
				entity.getQuestionText(),
				choices,
				entity.getAnswerIndex(),
				entity.getExplanation());
	}
}