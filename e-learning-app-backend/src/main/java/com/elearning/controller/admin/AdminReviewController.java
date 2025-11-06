package com.elearning.controller.admin;

import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class AdminReviewController {

    private final ReviewService reviewService;

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteAnyReview(@PathVariable Integer reviewId) {
        log.info("Admin/Teacher đang xóa review ID: {}", reviewId);

        reviewService.adminDeleteReview(reviewId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đã xóa đánh giá (với quyền Admin/Instructor)")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}