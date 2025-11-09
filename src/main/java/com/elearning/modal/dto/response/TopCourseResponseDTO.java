package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCourseResponseDTO {

    private Integer courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private Long studentCount;
    private BigDecimal totalRevenue;

    public TopCourseResponseDTO(Integer courseId, String courseTitle, String thumbnailUrl, BigDecimal totalRevenue) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.totalRevenue = totalRevenue;
        this.studentCount = 0L; // Sẽ được set sau nếu cần
    }


    public TopCourseResponseDTO(Integer courseId, String courseTitle, String thumbnailUrl, Long studentCount) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.studentCount = studentCount;
        this.totalRevenue = BigDecimal.ZERO;
    }
}