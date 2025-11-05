package com.elearning.modal.dto.response;

import com.elearning.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {

    private Integer id;
    private String questionText;
    private QuestionType questionType;
    private List<AnswerResponseDTO> answers;
}