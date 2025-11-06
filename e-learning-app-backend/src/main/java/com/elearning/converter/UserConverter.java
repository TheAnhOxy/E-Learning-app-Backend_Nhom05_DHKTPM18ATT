package com.elearning.converter;

import com.elearning.entity.*;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final ModelMapper modelMapper;

    public UserResponseDTO toDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public StudentResponseDTO toStudentDTO(User user) {
        StudentResponseDTO dto = modelMapper.map(user, StudentResponseDTO.class);
        if (user.getEnrollments() != null) {
            dto.setEnrolledCoursesCount(user.getEnrollments().size());
        } else {
            dto.setEnrolledCoursesCount(0);
        }
        return dto;
    }

    public StudentDetailResponseDTO toStudentDetailDTO(
            User user,
            List<Enrollment> enrollments,
            List<QuizAttempt> quizAttempts,
            List<Certificate> certificates
    ) {
        StudentDetailResponseDTO dto = modelMapper.map(user, StudentDetailResponseDTO.class);

        dto.setEnrolledCourses(enrollments.stream()
                .map(this::toEnrolledCourseDTO)
                .collect(Collectors.toList()));

        dto.setQuizAttempts(quizAttempts.stream()
                .map(this::toQuizAttemptDTO)
                .collect(Collectors.toList()));

        dto.setCertificates(certificates.stream()
                .map(this::toCertificateDTO)
                .collect(Collectors.toList()));
        dto.setEnrolledCoursesCount(enrollments.size());
        dto.setQuizAttemptsCount(quizAttempts.size());
        int completedCount = (int) enrollments.stream()
                .filter(this::isCourseCompleted)
                .count();
        dto.setCompletedCoursesCount(completedCount);

        return dto;
    }


    public void updateEntity(User user, StudentUpdateRequestDTO dto) {
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setAvatarUrl(dto.getAvatarUrl());
    }


    private EnrolledCourseResponseDTO toEnrolledCourseDTO(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        if (course == null) return null; // Trường hợp course đã bị xóa

        // Tính toán tiến độ
        Set<Progress> progresses = enrollment.getProgresses();
        Set<Lesson> allLessons = course.getSections().stream()
                .flatMap(section -> section.getLessons().stream())
                .collect(Collectors.toSet());

        int totalLessons = allLessons.size();
        long completedLessons = progresses.stream()
                .filter(Progress::getIsCompleted)
                .count();

        double progressPercent = (totalLessons > 0) ? ((double) completedLessons / totalLessons) * 100 : 0;

        return EnrolledCourseResponseDTO.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseThumbnailUrl(course.getThumbnailUrl())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .progressPercent(Math.round(progressPercent)) // Làm tròn
                .isCompleted(progressPercent >= 100)
                .build();
    }

    private boolean isCourseCompleted(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        if (course == null) return false;

        Set<Lesson> allLessons = course.getSections().stream()
                .flatMap(section -> section.getLessons().stream())
                .collect(Collectors.toSet());

        long completedLessons = enrollment.getProgresses().stream()
                .filter(Progress::getIsCompleted)
                .count();

        return allLessons.size() > 0 && completedLessons >= allLessons.size();
    }

    private QuizAttemptResponseDTO toQuizAttemptDTO(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        if (quiz == null) return null;

        return QuizAttemptResponseDTO.builder()
                .quizAttemptId(attempt.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .lessonId(quiz.getLesson() != null ? quiz.getLesson().getId() : null)
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .completedAt(attempt.getCompletedAt())
                .build();
    }

    private CertificateResponseDTO toCertificateDTO(Certificate cert) {
        Course course = cert.getCourse();
        if (course == null) return null;

        return CertificateResponseDTO.builder()
                .certificateId(cert.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .certificateCode(cert.getCertificateCode())
                .issueDate(cert.getIssueDate())
                .build();
    }

}