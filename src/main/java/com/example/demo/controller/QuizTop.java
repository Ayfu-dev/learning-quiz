package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuizTop {

	@GetMapping("/quiz")
	public String showQuestion() {
		return "quiz/top";
	}
}
