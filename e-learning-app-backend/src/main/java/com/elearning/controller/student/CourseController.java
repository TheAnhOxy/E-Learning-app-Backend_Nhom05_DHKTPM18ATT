package com.elearning.controller.student;

import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.modal.dto.response.MyCourseResponseDTO;
import com.elearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("studentCourseController")
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/recommended")
    public ResponseEntity<?> getRecommended() {
        List<CourseResponseDTO> recommendedCourses = courseService.getRecommendedCourses();
        if (recommendedCourses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Không có khóa học nào được đề xuất.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(recommendedCourses);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopular() {
        List<CourseResponseDTO> popularCourses = courseService.getPopularCourses();
        if (popularCourses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Không có khóa học phổ biến nào.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(popularCourses);
    }

    @GetMapping("/inspiring")
    public ResponseEntity<?> getInspiring() {
        List<CourseResponseDTO> inspiringCourses = courseService.getInspiringCourses();
        if (inspiringCourses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Không có khóa học truyền cảm hứng nào.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(inspiringCourses);
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getCoursesByCategory(
            @RequestParam("category_id") Integer categoryId,
            @RequestParam(value = "_page", defaultValue = "1") int page,
            @RequestParam(value = "_limit", defaultValue = "6") int limit) {
        try {
            Page<CourseResponseDTO> coursePage = courseService.getCoursesByCategoryId(categoryId, page, limit);
            if (coursePage.isEmpty()) {
                return ResponseEntity.ok("Không có khóa học nào trong danh mục này.");
            }
            return ResponseEntity.ok(Map.of(
                    "courses", coursePage.getContent(),
                    "total", coursePage.getTotalElements(),
                    "page", page,
                    "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy khóa học theo danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/by-instructor")
    public ResponseEntity<?> getCoursesByInstructor(
            @RequestParam("instructor_id") Integer instructorId,
            @RequestParam(value = "_page", defaultValue = "1") int page,
            @RequestParam(value = "_limit", defaultValue = "6") int limit) {
        try {
            Page<CourseResponseDTO> coursePage = courseService.getCourseByTeacherId(instructorId, page, limit);
            if (coursePage.isEmpty()) {
                return ResponseEntity.ok("Không có khóa học nào của giảng viên này.");
            }
            return ResponseEntity.ok(Map.of(
                    "courses", coursePage.getContent(),
                    "total", coursePage.getTotalElements(),
                    "page", page,
                    "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy khóa học theo giảng viên: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable("id") Integer id) {
        try {
            CourseResponseDTO course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Khóa học không tồn tại.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(course);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy chi tiết khóa học: " + e.getMessage());
        }
    }

    @GetMapping("/my-courses")
    public ResponseEntity<?> getCoursesByStudent(
            @RequestParam("student_id") Integer studentId,
            @RequestParam(value = "_page", defaultValue = "1") int page,
            @RequestParam(value = "_limit", defaultValue = "6") int limit) {
        try {
            Page<MyCourseResponseDTO> coursePage = courseService.getCoursesByStudentId(studentId, page, limit);
            if (coursePage.isEmpty()) {
                return ResponseEntity.ok("Học viên chưa mua khóa học nào.");
            }
            return ResponseEntity.ok(Map.of(
                    "myCourses", coursePage.getContent(),
                    "total", coursePage.getTotalElements(),
                    "page", page,
                    "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy khóa học của học viên: " + e.getMessage());
        }
    }

}
