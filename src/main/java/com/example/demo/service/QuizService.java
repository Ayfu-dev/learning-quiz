package com.example.demo.service;

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