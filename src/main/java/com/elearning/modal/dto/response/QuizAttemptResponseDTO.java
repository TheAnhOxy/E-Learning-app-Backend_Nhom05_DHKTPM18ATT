package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponseDTO {
    private Integer quizAttemptId;
    private Integer quizId;
    private String quizTitle;
    private Integer lessonId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime completedAt;
}