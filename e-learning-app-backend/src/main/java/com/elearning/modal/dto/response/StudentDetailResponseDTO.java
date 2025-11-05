package com.elearning.modal.dto.response;

import com.elearning.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentDetailResponseDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String avatarUrl;
    private LocalDateTime createdAt;
     private String status;

    private int enrolledCoursesCount;
    private int completedCoursesCount;
    private int quizAttemptsCount;

    private List<EnrolledCourseResponseDTO> enrolledCourses;
    private List<QuizAttemptResponseDTO> quizAttempts;
    private List<CertificateResponseDTO> certificates;
}