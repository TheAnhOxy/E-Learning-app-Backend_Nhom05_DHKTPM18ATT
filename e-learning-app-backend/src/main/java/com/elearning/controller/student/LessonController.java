package com.elearning.controller.student;

import com.elearning.modal.dto.response.LessonResponseDTO;
import com.elearning.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("studentLessonController")
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("{lesson_id}")
    public ResponseEntity<?> getLessonById( @PathVariable("lesson_id") Integer lessonId) {
        try{
            LessonResponseDTO lesson = lessonService.getLessonById(lessonId);
            return ResponseEntity.status(200).body(lesson);
        } catch (Exception e){
            return ResponseEntity.status(404).body("Không tìm thấy bài học với ID: " + lessonId);
        }
    }
}
