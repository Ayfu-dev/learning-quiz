package com.example.demo.model;

import java.util.List;

public class QuizSet {

	private String title;
	private List<Question> questions;

	public QuizSet(String title, List<Question> questions) {
		this.title = title;
		this.questions = List.copyOf(questions);
	}

	public String getTitle() {
		return title;
	}

	public List<Question> getQuestions() {
		return List.copyOf(questions);
	}
}
