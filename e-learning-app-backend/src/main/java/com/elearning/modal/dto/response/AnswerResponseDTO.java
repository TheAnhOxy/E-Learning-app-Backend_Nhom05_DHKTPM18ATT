package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {

    private Integer id;

    private String answerText;

    private Boolean isCorrect;
}