package com.elearning.service;

import com.elearning.modal.dto.response.ProgressResponseDTO;

import java.util.List;

public interface ProgressService {
    List<ProgressResponseDTO> getProgressByUserAndCourse(Integer userId, Integer courseId);
    void markLessonAsCompleted(Integer userId, Integer courseId, Integer lessonId);
}