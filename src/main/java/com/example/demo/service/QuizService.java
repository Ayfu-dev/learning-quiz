package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.model.Question;
import com.example.demo.model.QuizData;
import com.example.demo.model.QuizSet;
import com.example.demo.repository.JsonQuizRepository;

@Service
public class QuizService {

	private final Map<String, List<QuizSet>> quizSetMap;

	public QuizService(JsonQuizRepository repository) {

		QuizData quizData = repository.findAll();

		Map<String, List<QuizSet>> map = new HashMap<>();
		map.put(quizData.getCategory(), quizData.getQuizSets());

		quizSetMap = map;
	}

	public List<QuizSet> getQuizSets(String category) {
		List<QuizSet> sets = quizSetMap.get(category);

		if (sets == null) {
			throw new IllegalArgumentException("存在しないカテゴリ: " + category);
		}

		return sets;
	}

	public List<Question> getQuestions(String category, int setIndex) {
		return getQuizSets(category)
				.get(setIndex)
				.getQuestions();
	}

	public boolean checkAnswer(
			String category,
			int setIndex,
			int questionIndex,
			int userAnswer) {

		return getQuizSets(category)
				.get(setIndex)
				.getQuestions()
				.get(questionIndex)
				.getAnswerIndex() == userAnswer;
	}
}