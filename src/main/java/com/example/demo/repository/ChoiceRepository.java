package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Integer> {
}
