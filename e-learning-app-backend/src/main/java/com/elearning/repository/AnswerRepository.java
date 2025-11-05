package com.elearning.repository;

import com.elearning.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}