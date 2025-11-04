package com.elearning.modal.dto.response;

import com.elearning.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private Integer originalPrice;
    private CourseStatus status;
    private String videoPreviewUrl;
    private LocalDateTime createdAt;
    private Integer instructorId;
    private String instructorName;
    private String instructorAvatar;
    private Integer categoryId;
    private String categoryName;
}
