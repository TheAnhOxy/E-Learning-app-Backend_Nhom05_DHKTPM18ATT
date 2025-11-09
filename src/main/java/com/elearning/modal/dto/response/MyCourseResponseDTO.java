package com.elearning.modal.dto.response;

import lombok.Data;

@Data
public class MyCourseResponseDTO {
    private Integer id;
    private Integer studentId;
    private String title;
    private String thumbnailUrl;
    private Integer lessonCount;
    private Integer completedLessons;
    private Double progressPercentage;
}
