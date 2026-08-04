package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.service.QuizService;

@Controller
@RequestMapping("/quiz")
@SessionAttributes("userAnswers")
public class QuizController {

	private final QuizService quizService;

	public QuizController(QuizService quizService) {
		this.quizService = quizService;
	}

	@ModelAttribute("userAnswers")
	public List<Integer> userAnswers() {
		return new ArrayList<>();
	}
	
	@GetMapping("/{category}/list")
	public String showQuizList(@PathVariable String category, Model model) {
	    var sets = quizService.getQuizSets(category);
	    model.addAttribute("category", category);
	    model.addAttribute("quizSets", sets);
	    return "quiz/list";
	}

	// 問題を1問表示 カテゴリー、章、問題番号
	@GetMapping("/{category}/{setIndex}/{questionIndex}")
	public String showQuestion(
			@PathVariable String category,
			@PathVariable int setIndex,
			@PathVariable int questionIndex,
			Model model) {
		var questions = quizService.getQuestions(category, setIndex);

		if (questionIndex >= questions.size()) {
			return "redirect:/quiz/" + category + "/" + setIndex + "/result";
		}
		model.addAttribute("question", questions.get(questionIndex));
		model.addAttribute("category", category);
		model.addAttribute("setIndex", setIndex);
		model.addAttribute("questionIndex", questionIndex);

		return "quiz/quiz";
	}
	
	// 回答 → 正誤＋解説
	@PostMapping("/{category}/{setIndex}/{questionIndex}")
	public String checkAnswer(
			@PathVariable String category,
			@PathVariable int setIndex,
			@PathVariable int questionIndex,
			@RequestParam(value = "answer", required = false) Integer answer,
			@ModelAttribute("userAnswers") List<Integer> userAnswers,
			Model model) {
		var questions = quizService.getQuestions(category, setIndex);

		if (answer == null) {
			model.addAttribute("error", "回答を選択してください");
			model.addAttribute("question", questions.get(questionIndex));
			model.addAttribute("category", category);
			model.addAttribute("setIndex", setIndex);
			model.addAttribute("questionIndex", questionIndex);
			return "quiz/quiz";
		}

		if (userAnswers.size() <= questionIndex) {
			userAnswers.add(answer);
		} else {
			userAnswers.set(questionIndex, answer);
		}

		boolean correct = quizService.checkAnswer(category, setIndex, questionIndex, answer);

		model.addAttribute("question", questions.get(questionIndex));
		model.addAttribute("correct", correct);
		model.addAttribute("userAnswer", answer);
		model.addAttribute("category", category);
		model.addAttribute("setIndex", setIndex);
		model.addAttribute("questionIndex", questionIndex);

		return "quiz/answer";
	}

	// 最終結果
	@GetMapping("/{category}/{setIndex}/result")
	public String showResult(
			@PathVariable String category,
			@PathVariable int setIndex,
			@ModelAttribute("userAnswers") List<Integer> userAnswers,
			Model model) {

		var questions = quizService.getQuestions(category, setIndex);

//		採点
		int score = 0;
		for (int i = 0; i < questions.size(); i++) {
//		    userAnswers が足りない場合は不正解扱い（エラーにならない用の緊急回避）
		    if (i >= userAnswers.size()) {
		        continue;
		    }
		    if (questions.get(i).getAnswerIndex() == userAnswers.get(i)) {
		        score++;
		    }
		}

		model.addAttribute("score", score);
		model.addAttribute("total", questions.size());
		model.addAttribute("category", category);
		model.addAttribute("setIndex", setIndex);

		return "quiz/result";
	}
}
