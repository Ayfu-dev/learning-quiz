package com.example.demo.model;

import java.util.List;

public class Question {

	private String questionText;
	private List<String> choices;
	private int answerIndex;
	private String explanation;

//	そもそも変更する必要がないなら、変更できなくする方がシンプル
//	⇒setterは設定しない
	public String getQuestionText() {
		return questionText;
	}

	public List<String> getChoices() {
	    return List.copyOf(choices);
	}

	public int getAnswerIndex() {
		return answerIndex;
	}

	public String getExplanation() {
		return explanation;
	}

	public Question(String questionText, List<String> choices, int answerIndex, String explanation ){
		this.questionText = questionText;
		this.choices = List.copyOf(choices);
		this.answerIndex = answerIndex;
		this.explanation = explanation;
	}

}
