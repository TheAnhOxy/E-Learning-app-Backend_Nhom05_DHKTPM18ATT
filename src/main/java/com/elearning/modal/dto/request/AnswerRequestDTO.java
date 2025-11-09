package com.elearning.modal.dto.request;

import com.elearning.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDTO {
    private Integer id;

    @NotBlank(message = "Nội dung câu trả lời không được trống")
    private String answerText;

    @NotNull
    private Boolean isCorrect;
}