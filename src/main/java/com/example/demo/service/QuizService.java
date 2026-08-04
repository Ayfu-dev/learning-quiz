package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.model.Question;
import com.example.demo.model.QuizSet;

@Service
public class QuizService {
	private final Map<String, List<QuizSet>> quizSetMap;

	public QuizService() {
		QuizSet aws1 = new QuizSet(
				"1~6章", List.of(
						new Question("インターネットからアクセスできるWebサーバーを構築します。最初に作成するものはどれですか？",
								List.of("セキュリティグループ", "VPC", "EC2", "Internet Gateway"),
								1,
								"ネットワークの土台となるVPCを作成してから、サブネットやInternet Gatewayなどを設定します。"),
						new Question(
								"新入社員に同じ権限を付与したい場合、最も効率的な方法はどれですか？",
								List.of(
										"全員ルートユーザーを利用する",
										"IAMユーザーを作成し、それぞれに同じポリシーを付与する",
										"IAMグループを作成し、ポリシーを付与してユーザーを追加する",
										"EC2を作成する"),
								2,
								"IAMでは、同じ役割を持つユーザーはグループで管理するのが基本です。\n"
										+ "先にIAMグループへ必要なポリシーを付与しておけば、"
										+ "新しい社員はそのグループへ追加するだけで同じ権限を持たせることができます。")));
							
		QuizSet aws7 = new QuizSet(
				"7章", List.of(
						new Question(
								"ロードバランサー（ALB）の主な役割として最も適切なものはどれですか？",
								List.of("EC2インスタンスのOSを管理する", "複数のサーバーへリクエストを振り分ける", "データベースを自動でバックアップする", "DNS名を取得する"),
								1,
								"ロードバランサーは、利用者からのリクエストを複数のサーバーへ振り分け、負荷を分散する役割を持ちます。また、SSL通信の処理や不正なリクエストへの対策も行います。\n"
										+ "\n"
										+ "①：OS管理はEC2の役割\n"
										+ "③：バックアップはRDSなどの機能\n"
										+ "④：DNS名の管理はRoute 53の役割"),
						new Question(
								"",
								List.of("", "", "", ""),
								1,
								"")));
		quizSetMap = Map.of(
				"aws", List.of(aws1, aws7));

	}

	public List<QuizSet> getQuizSets(String category) {
		List<QuizSet> sets = quizSetMap.get(category);
		if (sets == null) {
			throw new IllegalArgumentException("存在しないカテゴリ: " + category);
		}
		return sets;
	}
	
    public List<Question> getQuestions(String category, int setIndex) {
        return getQuizSets(category).get(setIndex).getQuestions();
    }
    
	public boolean checkAnswer(String category, int setIndex, int questionIndex, int userAnswer) {
		return getQuizSets(category)
				.get(setIndex)
				.getQuestions()
				.get(questionIndex)
				.getAnswerIndex() == userAnswer;
	}

}

