package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {

    private Integer id;
    private String title;
    private Integer lessonId;
    private LocalDateTime createdAt;
    private List<QuestionResponseDTO> questions;
}