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
public class QuestionRequestDTO {
    private Integer id;

    @NotBlank(message = "Nội dung câu hỏi không được trống")
    private String questionText;

    @NotNull(message = "Loại câu hỏi không được trống")
    private QuestionType questionType;

    @Valid
    @NotEmpty(message = "Câu hỏi phải có ít nhất 1 câu trả lời")
    @Size(min = 2, message = "Câu hỏi phải có ít nhất 2 câu trả lời")
    private List<AnswerRequestDTO> answers;
}