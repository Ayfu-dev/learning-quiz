package com.example.demo.model;

import java.util.List;

public class Question {

    private String questionText;
    private List<String> choices;
    private int answerIndex;
    private String explanation;

    public Question() {
    }

    public Question(String questionText, List<String> choices, int answerIndex, String explanation) {
        this.questionText = questionText;
        this.choices = choices;
        this.answerIndex = answerIndex;
        this.explanation = explanation;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}


//package com.example.demo.model;
//
//import java.util.List;
//Immutable
//public class Question {
//
//	private String questionText;
//	private List<String> choices;
//	private int answerIndex;
//	private String explanation;
//
////	そもそも変更する必要がないなら、変更できなくする方がシンプル
////	⇒setterは設定しない
//	public String getQuestionText() {
//		return questionText;
//	}
//
//	public List<String> getChoices() {
//	    return List.copyOf(choices);
//	}
//
//	public int getAnswerIndex() {
//		return answerIndex;
//	}
//
//	public String getExplanation() {
//		return explanation;
//	}
//
//	public Question(String questionText, List<String> choices, int answerIndex, String explanation ){
//		this.questionText = questionText;
//		this.choices = List.copyOf(choices);
//		this.answerIndex = answerIndex;
//		this.explanation = explanation;
//	}
//
//}
