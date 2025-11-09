package com.elearning.controller.student;

import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkEnrollment(
            @RequestParam("user_id") Integer userId,
            @RequestParam("course_id") Integer courseId) {

        log.info("API check enrollment: user_id={}, course_id={}", userId, courseId);

        boolean isEnrolled = enrollmentService.isEnrolled(userId, courseId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(isEnrolled ? "Đã sở hữu khóa học" : "Chưa mua khóa học")
                .data(isEnrolled)
                .build();

        return ResponseEntity.ok(response);
    }
}
