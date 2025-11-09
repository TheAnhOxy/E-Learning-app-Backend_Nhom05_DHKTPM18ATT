package com.elearning.service.impl;

import com.elearning.converter.LessonConverter;
import com.elearning.entity.Lesson;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.LessonRequestDTO;
import com.elearning.modal.dto.response.LessonResponseDTO;
import com.elearning.repository.LessonRepository;
import com.elearning.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonConverter lessonConverter;

    @Override
    @Transactional
    public LessonResponseDTO createLesson(LessonRequestDTO lessonRequestDTO) {
        log.info("Bắt đầu tạo bài học (lesson)...");
        Lesson lesson = lessonConverter.toEntity(lessonRequestDTO);
        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Đã tạo bài học thành công với ID: {}", savedLesson.getId());
        return lessonConverter.toDTO(savedLesson);
    }

    @Override
    @Transactional
    public LessonResponseDTO updateLesson(Integer id, LessonRequestDTO lessonRequestDTO) {
        log.info("Bắt đầu cập nhật bài học ID: {}", id);
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow();
        lessonConverter.updateEntity(existingLesson, lessonRequestDTO);
        Lesson updatedLesson = lessonRepository.save(existingLesson);
        log.info("Đã cập nhật bài học thành công.");
        return lessonConverter.toDTO(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Integer id) {
        log.info("Bắt đầu xóa bài học ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow();

        lessonRepository.delete(lesson);
        log.info("Đã xóa bài học thành công.");
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponseDTO getLessonById(Integer id) {
        log.info("Đang lấy chi tiết bài học ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow();

        return lessonConverter.toDTO(lesson);
    }
}