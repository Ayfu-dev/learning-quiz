package com.example.demo.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@SessionAttributes({
    "userAnswers",
    "comprehensiveQuestions",
    "comprehensiveUserAnswers",
    "comprehensiveSelectedSetIndexes",
    "comprehensiveQuestionCount",
    "comprehensiveQuestionSetIndexes"
})
public class QuizController {

	private final QuizService quizService;

	public QuizController(QuizService quizService) {
		this.quizService = quizService;
	}

	@ModelAttribute("userAnswers")
	public List<Integer> userAnswers() {
		return new ArrayList<>();
	}
	
	@ModelAttribute("comprehensiveQuestions")
	public List<com.example.demo.model.Question> comprehensiveQuestions() {
	    return new ArrayList<>();
	}

	@ModelAttribute("comprehensiveUserAnswers")
	public List<Integer> comprehensiveUserAnswers() {
	    return new ArrayList<>();
	}
	
	@ModelAttribute("comprehensiveSelectedSetIndexes")
	public List<Integer> comprehensiveSelectedSetIndexes() {
	    return new ArrayList<>();
	}

	@ModelAttribute("comprehensiveQuestionCount")
	public Integer comprehensiveQuestionCount() {
	    return 0;
	}

	@ModelAttribute("comprehensiveQuestionSetIndexes")
	public List<Integer> comprehensiveQuestionSetIndexes() {
	    return new ArrayList<>();
	}
	
	@GetMapping("/{category}/list")
	public String showQuizList(@PathVariable String category, Model model) {
		var sets = quizService.getQuizSets(category);
		model.addAttribute("category", category);
		model.addAttribute("quizSets", sets);
		return "quiz/list";
	}
	//総合テスト
	@GetMapping("/{category}/comprehensive")
	public String showComprehensive(
	        @PathVariable String category,
	        Model model) {

	    var quizSets = quizService.getQuizSets(category);

	    model.addAttribute("category", category);
	    model.addAttribute("quizSets", quizSets);

	    return "quiz/comprehensive";
	}
	
	@PostMapping("/{category}/comprehensive")
	public String startComprehensive(
	        @PathVariable String category,
	        @RequestParam(value = "quizSet", required = false)
	        List<Integer> selectedSetIndexes,
	        @RequestParam int questionCount,
	        Model model) {

	    var quizSets = quizService.getQuizSets(category);

	    if (selectedSetIndexes == null || selectedSetIndexes.isEmpty()) {

	        model.addAttribute("error",
	                "出題範囲を1つ以上選択してください");

	        model.addAttribute("category", category);
	        model.addAttribute("quizSets", quizSets);

	        return "quiz/comprehensive";
	    }

	    selectedSetIndexes = selectedSetIndexes.stream()
	            .distinct()
	            .toList();
	    
	    int maxQuestionCount = 0;

	    for (Integer setIndex : selectedSetIndexes) {

	        if (setIndex >= 0 && setIndex < quizSets.size()) {
	            maxQuestionCount +=
	                    quizSets.get(setIndex).getQuestions().size();
	        }
	    }

	    if (questionCount > maxQuestionCount) {

	        model.addAttribute("error",
	                "出題数が選択した問題数を超えています");

	        model.addAttribute("category", category);
	        model.addAttribute("quizSets", quizSets);

	        return "quiz/comprehensive";
	    }

	    if (questionCount <= 0) {

	        model.addAttribute("error",
	                "出題数は1問以上にしてください");

	        model.addAttribute("category", category);
	        model.addAttribute("quizSets", quizSets);

	        return "quiz/comprehensive";
	    }

	    List<Integer> questionSetIndexes =
	            new ArrayList<>();

	    List<com.example.demo.model.Question> questions =
	            quizService.createComprehensiveQuestions(
	                    category,
	                    selectedSetIndexes,
	                    questionCount,
	                    questionSetIndexes);

	    model.addAttribute(
	            "comprehensiveSelectedSetIndexes",
	            new ArrayList<>(selectedSetIndexes));

	    model.addAttribute(
	            "comprehensiveQuestionCount",
	            questionCount);

	    model.addAttribute(
	            "comprehensiveUserAnswers",
	            new ArrayList<Integer>());

	    model.addAttribute(
	            "comprehensiveQuestions",
	            questions);
	    
	    model.addAttribute(
	            "comprehensiveQuestionSetIndexes",
	            questionSetIndexes);

	    return "redirect:/quiz/" + category + "/comprehensive/0";
	}
	
	//総合テスト問題
	@GetMapping("/{category}/comprehensive/{questionIndex}")
	public String showComprehensiveQuestion(
	        @PathVariable String category,
	        @PathVariable int questionIndex,
	        @ModelAttribute("comprehensiveQuestions")
	        List<com.example.demo.model.Question> questions,
	        Model model) {

	    if (questions.isEmpty()) {
	        return "redirect:/quiz/" + category + "/list";
	    }

	    if (questionIndex >= questions.size()) {
	        return "redirect:/quiz/" + category + "/comprehensive/result";
	    }

	    model.addAttribute("question", questions.get(questionIndex));
	    model.addAttribute("category", category);
	    model.addAttribute("questionIndex", questionIndex);
	    model.addAttribute("total", questions.size());

	    return "quiz/comprehensive-quiz";
	}
	
	@PostMapping("/{category}/comprehensive/{questionIndex}")
	public String checkComprehensiveAnswer(
	        @PathVariable String category,
	        @PathVariable int questionIndex,
	        @RequestParam(value = "answer", required = false) Integer answer,
	        @ModelAttribute("comprehensiveQuestions")
	        List<com.example.demo.model.Question> questions,
	        @ModelAttribute("comprehensiveUserAnswers")
	        List<Integer> userAnswers,
	        Model model) {

	    if (answer == null) {
	        model.addAttribute("error", "回答を選択してください");
	        model.addAttribute("question", questions.get(questionIndex));
	        model.addAttribute("category", category);
	        model.addAttribute("questionIndex", questionIndex);
	        model.addAttribute("total", questions.size());

	        return "quiz/comprehensive-quiz";
	    }

	    // 回答を保存
	    if (userAnswers.size() <= questionIndex) {
	        userAnswers.add(answer);
	    } else {
	        userAnswers.set(questionIndex, answer);
	    }

	    var question = questions.get(questionIndex);

	    boolean correct =
	            question.getAnswerIndex() == answer;

	    model.addAttribute("question", question);
	    model.addAttribute("correct", correct);
	    model.addAttribute("userAnswer", answer);
	    model.addAttribute("category", category);
	    model.addAttribute("questionIndex", questionIndex);
	    model.addAttribute("total", questions.size());

	    return "quiz/comprehensive-answer";
	}
	//総合テスト結果
	@GetMapping("/{category}/comprehensive/result")
	public String showComprehensiveResult(
	        @PathVariable String category,
	        @ModelAttribute("comprehensiveQuestions")
	        List<com.example.demo.model.Question> questions,
	        @ModelAttribute("comprehensiveUserAnswers")
	        List<Integer> userAnswers,
	        @ModelAttribute("comprehensiveQuestionSetIndexes")
	        List<Integer> questionSetIndexes,
	        Model model) {

	    if (questions.isEmpty()) {
	        return "redirect:/quiz/" + category + "/list";
	    }

	    int score = 0;

	    for (int i = 0; i < questions.size(); i++) {
	        if (i < userAnswers.size()
	                && userAnswers.get(i) != null
	                && questions.get(i).getAnswerIndex()
	                        == userAnswers.get(i)) {
	            score++;
	        }
	    }

	    Map<Integer, int[]> setResults = new LinkedHashMap<>();

	    for (int i = 0; i < questions.size(); i++) {

	        if (i >= questionSetIndexes.size()) {
	            continue;
	        }

	        int setIndex = questionSetIndexes.get(i);

	        setResults.putIfAbsent(setIndex, new int[2]);

	        // 出題数
	        setResults.get(setIndex)[1]++;

	        // 正解数
	        if (i < userAnswers.size()
	                && userAnswers.get(i) != null
	                && questions.get(i).getAnswerIndex()
	                        == userAnswers.get(i)) {

	            setResults.get(setIndex)[0]++;
	        }
	    }
	    model.addAttribute("score", score);
	    model.addAttribute("total", questions.size());
	    model.addAttribute("questions", questions);
	    model.addAttribute("userAnswers", userAnswers);
	    model.addAttribute("setResults", setResults);

	    return "quiz/comprehensive-result";
	}
	//総合テスト結果_個別問題確認
	@GetMapping("/{category}/comprehensive/review/{questionIndex}")
	public String reviewComprehensiveQuestion(
	        @PathVariable String category,
	        @PathVariable int questionIndex,
	        @ModelAttribute("comprehensiveQuestions")
	        List<com.example.demo.model.Question> questions,
	        @ModelAttribute("comprehensiveUserAnswers")
	        List<Integer> userAnswers,
	        Model model) {

	    if (questionIndex < 0 || questionIndex >= questions.size()) {
	        return "redirect:/quiz/" + category + "/comprehensive/result";
	    }

	    var question = questions.get(questionIndex);

	    Integer userAnswer = null;

	    if (questionIndex < userAnswers.size()) {
	        userAnswer = userAnswers.get(questionIndex);
	    }

	    boolean correct =
	            userAnswer != null
	            && question.getAnswerIndex() == userAnswer;

	    model.addAttribute("question", question);
	    model.addAttribute("userAnswer", userAnswer);
	    model.addAttribute("correct", correct);
	    model.addAttribute("category", category);
	    model.addAttribute("questionIndex", questionIndex);
	    model.addAttribute("total", questions.size());

	    return "quiz/comprehensive-review";
	}
	
	@GetMapping("/{category}/comprehensive/wrong")
	public String showWrongComprehensiveQuestions(
	        @PathVariable String category,
	        @ModelAttribute("comprehensiveQuestions")
	        List<com.example.demo.model.Question> questions,
	        @ModelAttribute("comprehensiveUserAnswers")
	        List<Integer> userAnswers,
	        Model model) {

	    List<Integer> wrongIndexes = new ArrayList<>();

	    for (int i = 0; i < questions.size(); i++) {

	        if (i >= userAnswers.size()
	                || userAnswers.get(i) == null
	                || questions.get(i).getAnswerIndex()
	                    != userAnswers.get(i)) {

	            wrongIndexes.add(i);
	        }
	    }

	    model.addAttribute("questions", questions);
	    model.addAttribute("wrongIndexes", wrongIndexes);
	    model.addAttribute("category", category);

	    return "quiz/comprehensive-wrong";
	}
	
	//同範囲ランダム出題
	@PostMapping("/{category}/comprehensive/retry")
	public String retryComprehensive(
	        @PathVariable String category,
	        @ModelAttribute("comprehensiveSelectedSetIndexes")
	        List<Integer> selectedSetIndexes,
	        @ModelAttribute("comprehensiveQuestionCount")
	        Integer questionCount,
	        Model model) {

	    if (selectedSetIndexes == null
	            || selectedSetIndexes.isEmpty()
	            || questionCount == null
	            || questionCount <= 0) {

	        return "redirect:/quiz/" + category + "/comprehensive";
	    }

	    List<Integer> questionSetIndexes =
	            new ArrayList<>();

	    List<com.example.demo.model.Question> questions =
	            quizService.createComprehensiveQuestions(
	                    category,
	                    selectedSetIndexes,
	                    questionCount,
	                    questionSetIndexes);
	    model.addAttribute(
	            "comprehensiveQuestions",
	            questions);

	    model.addAttribute(
	            "comprehensiveUserAnswers",
	            new ArrayList<Integer>());

	    return "redirect:/quiz/" + category + "/comprehensive/0";
	}
	
	// 問題を1問表示 カテゴリー、章、問題番号 競合を防ぐためadminをカテゴリーとして認識しないようにする
	@GetMapping("/{category:^(?!admin$).+}/{setIndex}/{questionIndex}")
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

	// 回答 → 正誤＋解説 競合を防ぐためadminをカテゴリーとして認識しないようにする
	@PostMapping("/{category:^(?!admin$).+}/{setIndex}/{questionIndex}")
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
