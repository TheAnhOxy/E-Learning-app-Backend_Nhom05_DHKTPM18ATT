package com.elearning.service;


import com.elearning.modal.dto.request.LessonRequestDTO;
import com.elearning.modal.dto.response.LessonResponseDTO;

public interface LessonService {
    LessonResponseDTO createLesson(LessonRequestDTO lessonRequestDTO);
    LessonResponseDTO updateLesson(Integer id, LessonRequestDTO lessonRequestDTO);
    void deleteLesson(Integer id);
    LessonResponseDTO getLessonById(Integer id);
}