package com.elearning.service;


import com.elearning.modal.dto.request.QuizRequestDTO;
import com.elearning.modal.dto.response.QuizResponseDTO;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO quizRequestDTO);
    QuizResponseDTO updateQuiz(Integer id, QuizRequestDTO quizRequestDTO);
    void deleteQuiz(Integer id);
    QuizResponseDTO getQuizById(Integer id);
}