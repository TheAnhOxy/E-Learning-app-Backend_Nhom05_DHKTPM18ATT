package com.elearning.repository;

import com.elearning.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {
    @Query("SELECT qa FROM QuizAttempt qa JOIN FETCH qa.quiz q WHERE qa.user.id = :userId ORDER BY qa.completedAt DESC")
    List<QuizAttempt> findAllWithQuizByUserId(Integer userId);
}