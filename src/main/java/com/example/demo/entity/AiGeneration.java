package com.example.demo.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_generation")
public class AiGeneration {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@Column(nullable = false, length = 500)
	private String theme;
	
	@Column(name = "question_count", nullable = false)
	private Integer questionCount;
	
	@Column(nullable = false)
	private Integer difficulty;
	
	@Column(name = "additional_instruction", length = 500)
	private String additionalInstruction;
	
	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;

	public AiGeneration() {
	}
	
	public AiGeneration(Integer id, Category category, String theme, Integer questionCount, Integer difficulty,
			String additionalInstruction, Timestamp createdAt) {
		this.id = id;
		this.category = category;
		this.theme = theme;
		this.questionCount = questionCount;
		this.difficulty = difficulty;
		this.additionalInstruction = additionalInstruction;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public Category getCategory() {
		return category;
	}

	public String getTheme() {
		return theme;
	}

	public Integer getQuestionCount() {
		return questionCount;
	}

	public Integer getDifficulty() {
		return difficulty;
	}

	public String getAdditionalInstruction() {
		return additionalInstruction;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	
}
