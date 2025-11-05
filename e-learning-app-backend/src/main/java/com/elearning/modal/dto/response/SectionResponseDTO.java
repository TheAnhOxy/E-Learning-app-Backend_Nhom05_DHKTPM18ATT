package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponseDTO {
    private Integer id;
    private String title;
    private Integer orderIndex;
    private Integer courseId;
    private List<LessonResponseDTO> lessons;
}