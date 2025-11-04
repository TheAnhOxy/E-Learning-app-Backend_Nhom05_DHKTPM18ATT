package com.elearning.modal.dto.search;

import com.elearning.enums.CourseStatus;
import lombok.Data;

@Data
public class CourseSearchRequest {
    private String title;
    private Integer categoryId;
    private Integer instructorId;
    private CourseStatus status;


     private int page = 0;
     private int size = 10;
}