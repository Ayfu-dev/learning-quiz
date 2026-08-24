package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Question;
import com.example.demo.entity.QuizSet;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizSetRepository;
import com.example.demo.service.QuizAdminService;

@Controller
@RequestMapping("/quiz/admin")
public class QuizAdminController {

	private final QuizAdminService quizAdminService;
	private final QuizSetRepository quizSetRepository;
	private final QuestionRepository questionRepository;
	private final CategoryRepository categoryRepository;

	public QuizAdminController(
			QuizAdminService quizAdminService,
			QuizSetRepository quizSetRepository,
			QuestionRepository questionRepository,
			CategoryRepository categoryRepository) {

		this.quizAdminService = quizAdminService;
		this.quizSetRepository = quizSetRepository;
		this.questionRepository = questionRepository;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public String showAdmin() {
		return "admin/admin";
	}

	@GetMapping("/quiz_set")
	public String showQuizSetList(Model model) {

		model.addAttribute(
				"quizSets",
				quizSetRepository.findAll());

		return "admin/quiz-set-list";
	}

	@GetMapping("/quiz_set/add")
	public String showQuizSetAdd(Model model) {

		model.addAttribute(
				"categories",
				categoryRepository.findAll());

		return "admin/quiz-set-add";
	}

	@PostMapping("/quiz_set/add")
	public String addQuizSet(
			@RequestParam Integer categoryId,
			@RequestParam String title) {

		quizAdminService.addQuizSet(
				categoryId,
				title);

		return "redirect:/quiz/admin/quiz_set";
	}

	@GetMapping("/quiz_set/edit")
	public String showQuizSetEdit(
			@RequestParam Integer id,
			Model model) {

		QuizSet quizSet = quizSetRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しないクイズセット: " + id));

		model.addAttribute("quizSet", quizSet);
		model.addAttribute(
				"categories",
				categoryRepository.findAll());

		return "admin/quiz-set-edit";
	}

	@PostMapping("/quiz_set/edit")
	public String editQuizSet(
			@RequestParam Integer id,
			@RequestParam Integer categoryId,
			@RequestParam String title) {

		quizAdminService.editQuizSet(
				id,
				categoryId,
				title);

		return "redirect:/quiz/admin/quiz_set";
	}

	@PostMapping("/quiz_set/delete")
	public String deleteQuizSet(@RequestParam Integer id) {

		quizAdminService.deleteQuizSet(id);

		return "redirect:/quiz/admin/quiz_set";
	}

	//  以降問題管理
	//  問題追加
	@GetMapping("/question/add")
	public String showQuestionAdd(Model model) {
		model.addAttribute(
				"quizSets",
				quizSetRepository.findAll());
		return "admin/question-add";
	}

	@PostMapping("/question/add")
	public String addQuestion(
			@RequestParam Integer quizSetId,
			@RequestParam String questionText,
			@RequestParam String[] choiceText,
			@RequestParam Integer answerIndex,
			@RequestParam String explanation) {

		quizAdminService.addQuestion(
				quizSetId,
				questionText,
				choiceText,
				answerIndex,
				explanation);

		return "redirect:/quiz/admin";
	}

	@GetMapping("/question")
	public String showQuestionList(
	        @RequestParam(required = false) Integer categoryId,
	        @RequestParam(required = false) Integer quizSetId,
	        Model model) {

	    List<Question> questions;

	    if (categoryId != null && quizSetId != null) {

	        questions = questionRepository
	                .findByQuizSetCategoryIdAndQuizSetId(
	                        categoryId,
	                        quizSetId);

	    } else if (categoryId != null) {

	        questions = questionRepository
	                .findByQuizSetCategoryId(categoryId);

	    } else if (quizSetId != null) {

	        questions = questionRepository
	                .findByQuizSetId(quizSetId);

	    } else {

	        questions = questionRepository.findAll();
	    }

	    // カテゴリ
	    model.addAttribute(
	            "categories",
	            categoryRepository.findAll());

	    // カテゴリが選択されていれば、そのカテゴリの章だけ取得
	    List<QuizSet> quizSets;

	    if (categoryId != null) {

	        quizSets = quizSetRepository
	                .findByCategoryId(categoryId);

	    } else {

	        quizSets = quizSetRepository.findAll();
	    }

	    model.addAttribute("quizSets", quizSets);

	    model.addAttribute("questions", questions);
	    model.addAttribute("selectedCategoryId", categoryId);
	    model.addAttribute("selectedQuizSetId", quizSetId);

	    return "admin/question-list";
	}
	@GetMapping("/question/edit")
	public String showQuestionEdit(
			@RequestParam Integer id,
			Model model) {

		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"存在しない問題: " + id));

		model.addAttribute("question", question);
		model.addAttribute("quizSets", quizSetRepository.findAll());

		return "admin/question-edit";
	}

	@PostMapping("/question/edit")
	public String editQuestion(
			@RequestParam Integer id,
			@RequestParam Integer quizSetId,
			@RequestParam String questionText,
			@RequestParam String[] choiceText,
			@RequestParam Integer answerIndex,
			@RequestParam String explanation) {

		quizAdminService.editQuestion(
				id,
				quizSetId,
				questionText,
				choiceText,
				answerIndex,
				explanation);

		return "redirect:/quiz/admin/question";
	}

	@PostMapping("/question/delete")
	public String deleteQuestion(@RequestParam Integer id) {

		quizAdminService.deleteQuestion(id);

		return "redirect:/quiz/admin/question";
	}

}
