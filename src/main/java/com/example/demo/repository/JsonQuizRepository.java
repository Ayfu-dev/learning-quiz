package com.example.demo.repository;

import java.io.InputStream;

import org.springframework.stereotype.Repository;

import com.example.demo.model.QuizData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class JsonQuizRepository {

	public QuizData findAll() {

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			InputStream inputStream = getClass().getResourceAsStream("/quiz/aws.json");

			if (inputStream == null) {
				throw new IllegalStateException("JSONファイルが見つかりません");
			}

			return objectMapper.readValue(inputStream, QuizData.class);

		} catch (Exception e) {
			throw new IllegalStateException(
					"クイズデータの読み込みに失敗しました", e);
		}
	}
}