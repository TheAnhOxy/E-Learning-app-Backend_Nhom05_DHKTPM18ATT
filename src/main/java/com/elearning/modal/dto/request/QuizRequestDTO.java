package com.elearning.modal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDTO {

    @NotBlank(message = "Tiêu đề Quiz không được trống")
    private String title;

    @NotNull(message = "ID Bài học (Lesson) không được trống")
    private Integer lessonId;

    @Valid
    @NotEmpty(message = "Quiz phải có ít nhất 1 câu hỏi")
    private List<QuestionRequestDTO> questions;
}