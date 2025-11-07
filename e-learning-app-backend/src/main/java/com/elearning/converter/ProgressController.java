package com.elearning.controller.student;

import com.elearning.modal.dto.response.ProgressResponseDTO;
import com.elearning.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/by-enrollment")
    public ResponseEntity<List<ProgressResponseDTO>> getProgress(
            @RequestParam("user_id") Integer userId,
            @RequestParam("course_id") Integer courseId) {

        List<ProgressResponseDTO> progress = progressService
                .getProgressByUserAndCourse(userId, courseId);

        return ResponseEntity.ok(progress);
    }

    @PostMapping("/complete")
    public ResponseEntity<String> markComplete(
            @RequestParam("user_id") Integer userId,
            @RequestParam("course_id") Integer courseId,
            @RequestParam("lesson_id") Integer lessonId) {

        progressService.markLessonAsCompleted(userId, courseId, lessonId);
        return ResponseEntity.ok("Đánh dấu hoàn thành thành công");
    }
}