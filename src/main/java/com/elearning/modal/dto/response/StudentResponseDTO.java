package com.elearning.modal.dto.response;

import com.elearning.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentResponseDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String avatarUrl;
    private LocalDateTime createdAt;

    private int enrolledCoursesCount; // Số khóa học đã tham gia
}