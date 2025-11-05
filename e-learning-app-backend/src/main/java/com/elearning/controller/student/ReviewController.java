package com.elearning.controller.student;


import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import com.elearning.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    private Integer getAuthenticatedUserId(UserDetails userDetails) {
        log.warn("ĐANG GIẢ LẬP USER ID = 1 (student01). Cần thay thế bằng logic bảo mật thật!");
        return 1;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse> getReviewsByCourse(
            @PathVariable Integer courseId,
            @PageableDefault(size = 5, sort = "createdAt") Pageable pageable
    ) {
        log.info("Nhận yêu cầu lấy reviews cho khóa học ID: {}", courseId);
        var reviewPage = reviewService.getReviewsByCourseId(courseId, pageable);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đánh giá thành công")
                .data(reviewPage)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-review/{courseId}")
    public ResponseEntity<ApiResponse> getMyReview(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        log.info("Student ID {} lấy review của mình cho khóa học ID: {}", userId, courseId);

        var reviewData = reviewService.getMyReviewForCourse(courseId, userId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá của bạn thành công")
                .data(reviewData)
                .build();
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        log.info("Student ID {} tạo review cho khóa học ID: {}", userId, reviewRequestDTO.getCourseId());

        var createdReview = reviewService.createReview(reviewRequestDTO, userId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo đánh giá thành công!")
                .data(createdReview)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> updateMyReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        log.info("Student ID {} cập nhật review ID: {}", userId, reviewId);

        var updatedReview = reviewService.updateReview(reviewId, reviewRequestDTO, userId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật đánh giá thành công")
                .data(updatedReview)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteMyReview(
            @PathVariable Integer reviewId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        log.info("Student ID {} xóa review ID: {}", userId, reviewId);

        reviewService.deleteReview(reviewId, userId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa đánh giá thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------
    @GetMapping("/by-course")
    public ResponseEntity<?> getAllReviewByCourseId(
            @RequestParam("course_id") Integer categoryId,
            @RequestParam(value = "_page", defaultValue = "1") int page,
            @RequestParam(value = "_limit", defaultValue = "6") int limit) {
        try {
            Page<ReviewResponseDTO> reviewPage = reviewService.getAllReviewByCourseId(categoryId, page, limit);
            if (reviewPage.isEmpty()) {
                return ResponseEntity.ok("Không có đánh giá nào trong khóa học này.");
            }
            return ResponseEntity.ok(Map.of(
                    "reviews", reviewPage.getContent(),
                    "total", reviewPage.getTotalElements(),
                    "page", page,
                    "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy đánh giá theo khóa học: " + e.getMessage());
        }
    }

}