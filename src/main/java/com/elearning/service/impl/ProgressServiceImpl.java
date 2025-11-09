package com.elearning.service.impl;

import com.elearning.entity.Enrollment;
import com.elearning.entity.Lesson;
import com.elearning.entity.Progress;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.response.ProgressResponseDTO;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.LessonRepository;
import com.elearning.repository.ProgressRepository;
import com.elearning.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressServiceImpl implements ProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProgressResponseDTO> getProgressByUserAndCourse(Integer userId, Integer courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa đăng ký khóa học này"));

        return progressRepository.findByEnrollmentId(enrollment.getId())
                .stream()
                .map(progress -> new ProgressResponseDTO(
                        progress.getLesson().getId(),
                        progress.getIsCompleted()
                ))
                .toList();
    }

    @Override
    public void markLessonAsCompleted(Integer userId, Integer courseId, Integer lessonId) {
        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa đăng ký khóa học"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        Progress progress = progressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setEnrollment(enrollment);
                    newProgress.setLesson(lesson);
                    newProgress.setIsCompleted(false);
                    return newProgress;
                });

        progress.setIsCompleted(true);
        progressRepository.save(progress);
    }
}