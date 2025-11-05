package com.elearning.controller;

import com.elearning.modal.dto.request.LessonRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/lessons") 
@RequiredArgsConstructor
@Slf4j
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<ApiResponse> createLesson(@Valid @RequestBody LessonRequestDTO lessonRequestDTO) {
        log.info("Nhận yêu cầu tạo bài học mới: {}", lessonRequestDTO.getTitle());
        var lessonData = lessonService.createLesson(lessonRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo bài học thành công!")
                .data(lessonData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLessonById(@PathVariable Integer id) {
        log.info("Nhận yêu cầu lấy chi tiết bài học ID: {}", id);
        var lessonData = lessonService.getLessonById(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết bài học thành công")
                .data(lessonData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLesson(
            @PathVariable Integer id,
            @Valid @RequestBody LessonRequestDTO lessonRequestDTO
    ) {
        log.info("Nhận yêu cầu cập nhật bài học ID: {}", id);
        var updatedLessonData = lessonService.updateLesson(id, lessonRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật bài học thành công")
                .data(updatedLessonData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLesson(@PathVariable Integer id) {
        log.info("Nhận yêu cầu xóa bài học ID: {}", id);
        lessonService.deleteLesson(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa bài học thành công")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}