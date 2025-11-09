package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProgressResponseDTO {
    private Integer lessonId;
    private Boolean isCompleted;
}