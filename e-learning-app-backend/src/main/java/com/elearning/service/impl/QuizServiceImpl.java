package com.elearning.service.impl;

import com.elearning.converter.QuizConverter;

import com.elearning.entity.Quiz;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.QuizRequestDTO;
import com.elearning.modal.dto.response.QuizResponseDTO;
import com.elearning.repository.QuizRepository;
import com.elearning.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizConverter quizConverter;

    @Override
    @Transactional
    public QuizResponseDTO createQuiz(QuizRequestDTO quizRequestDTO) {
        log.info("Bắt đầu tạo quiz mới...");
        Quiz quiz = quizConverter.toEntity(quizRequestDTO);
        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Đã tạo quiz thành công với ID: {}", savedQuiz.getId());
        return quizConverter.toDTO(savedQuiz);
    }

    @Override
    @Transactional
    public QuizResponseDTO updateQuiz(Integer id, QuizRequestDTO quizRequestDTO) {
        log.info("Bắt đầu cập nhật quiz ID: {}", id);
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow();
        quizConverter.updateEntity(existingQuiz, quizRequestDTO);
        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        log.info("Đã cập nhật quiz thành công.");
        return quizConverter.toDTO(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Integer id) {
        log.info("Bắt đầu xóa quiz ID: {}", id);
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow();
        quizRepository.delete(quiz);
        log.info("Đã xóa quiz thành công.");
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Integer id) {
        log.info("Đang lấy chi tiết quiz ID: {}", id);
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow();
        return quizConverter.toDTO(quiz);
    }
}