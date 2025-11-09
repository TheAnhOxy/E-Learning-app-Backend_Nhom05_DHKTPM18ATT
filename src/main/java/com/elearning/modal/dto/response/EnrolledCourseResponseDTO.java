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
public class EnrolledCourseResponseDTO {
    private Integer courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    private LocalDateTime enrollmentDate;
    private double progressPercent; // Thống kê: % tiến độ
    private boolean isCompleted; // Thống kê: đã xong hay chưa
}