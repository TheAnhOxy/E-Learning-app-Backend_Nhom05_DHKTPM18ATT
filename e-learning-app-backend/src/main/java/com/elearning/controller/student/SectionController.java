package com.elearning.controller.student;

import com.elearning.modal.dto.response.SectionResponseDTO;
import com.elearning.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("studentSectionController")
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @GetMapping("/by-course")
    public ResponseEntity<?> getSectionsByCourse(
            @RequestParam("course_id") Integer courseId) {
        List<SectionResponseDTO> sections = sectionService.getSectionsByCourse(courseId);
        if (sections.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Không có phần nào trong khóa học này.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sections);
    }
}
