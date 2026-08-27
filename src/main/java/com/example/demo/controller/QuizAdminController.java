package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
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
	
	//パスワード設定 application.properties
	@Value("${admin.password}")
	private String adminPassword;
	
	private boolean isAuthenticated(HttpSession session) {

		Boolean authenticated =
				(Boolean) session.getAttribute("adminAuthenticated");

		return Boolean.TRUE.equals(authenticated);
	}
	
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
	public String showAdmin(HttpSession session) {

		Boolean authenticated =
				(Boolean) session.getAttribute("adminAuthenticated");

		if (!Boolean.TRUE.equals(authenticated)) {
			return "redirect:/quiz/admin/login";
		}

		return "admin/admin";
	}

	@GetMapping("/login")
	public String showLogin() {
		return "admin/login";
	}
	
	@PostMapping("/login")
	public String login(
			@RequestParam String password,
			HttpSession session,
			Model model) {
		
		if (adminPassword.equals(password)) {

			session.setAttribute("adminAuthenticated", true);

			return "redirect:/quiz/admin";
		}

		model.addAttribute("error", "パスワードが違います");

		return "admin/login";
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
			@RequestParam(required = false) String keyword,
			Model model) {

		List<Question> questions;

		boolean hasKeyword = keyword != null && !keyword.isBlank();

		if (hasKeyword) {

			keyword = keyword.trim();

			if (categoryId != null && quizSetId != null) {

				questions = questionRepository
						.findByQuizSetCategoryIdAndQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
								categoryId,
								quizSetId,
								keyword);

			} else if (categoryId != null) {

				questions = questionRepository
						.findByQuizSetCategoryIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
								categoryId,
								keyword);

			} else if (quizSetId != null) {

				questions = questionRepository
						.findByQuizSetIdAndQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
								quizSetId,
								keyword);

			} else {

				questions = questionRepository
						.findByQuestionTextContainingIgnoreCaseOrderByQuestionOrderAsc(
								keyword);
			}

		} else {

			if (categoryId != null && quizSetId != null) {

				questions = questionRepository
						.findByQuizSetCategoryIdAndQuizSetIdOrderByQuestionOrderAsc(
								categoryId,
								quizSetId);

			} else if (categoryId != null) {

				questions = questionRepository
						.findByQuizSetCategoryIdOrderByQuestionOrderAsc(
								categoryId);

			} else if (quizSetId != null) {

				questions = questionRepository
						.findByQuizSetIdOrderByQuestionOrderAsc(
								quizSetId);

			} else {
				questions = questionRepository
						.findAllByOrderByQuestionOrderAsc();
			}
		}
			

		// カテゴリ
		model.addAttribute(
				"categories",
				categoryRepository.findAll());

		// 章
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
		model.addAttribute("keyword", keyword);

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
	
	@PostMapping("/question/order")
	public String updateQuestionOrder(
	        @RequestParam Integer quizSetId,
	        @RequestParam List<Integer> questionIds,
	        @RequestParam List<Integer> questionOrders,
	        Model model) {

	    try {
	        quizAdminService.updateQuestionOrder(
	                quizSetId,
	                questionIds,
	                questionOrders);

	        return "redirect:/quiz/admin/question?quizSetId=" + quizSetId;

	    } catch (IllegalArgumentException e) {

	        model.addAttribute("errorMessage", e.getMessage());

	        // エラー時に一覧を再表示するためのデータ
	        model.addAttribute(
	                "questions",
	                questionRepository.findByQuizSetId(quizSetId));

	        model.addAttribute(
	                "categories",
	                categoryRepository.findAll());

	        model.addAttribute(
	                "quizSets",
	                quizSetRepository.findAll());

	        model.addAttribute(
	                "selectedQuizSetId",
	                quizSetId);

	        return "admin/question-list";
	    }
	}
}
