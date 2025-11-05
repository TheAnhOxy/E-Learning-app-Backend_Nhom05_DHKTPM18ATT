package com.elearning.repository;

import com.elearning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface QuestionRepository extends JpaRepository<Question, Integer> {
}