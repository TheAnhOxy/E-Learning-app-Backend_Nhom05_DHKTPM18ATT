package com.elearning.controller.student;



import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import com.elearning.service.CustomUserDetails;
import com.elearning.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse> getReviewsByCourse(
            @PathVariable Integer courseId
    ) {
        log.info("Public request: Lấy reviews cho khóa học ID: {}", courseId);
        List<ReviewResponseDTO> reviewList = reviewService.getReviewsByCourseId(courseId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đánh giá thành công")
                .data(reviewList)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-review/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> getMyReview(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
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
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
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
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> updateMyReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
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
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> deleteMyReview(
            @PathVariable Integer reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
        log.info("Student ID {} xóa review ID: {}", userId, reviewId);

        reviewService.deleteReview(reviewId, userId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa đánh giá thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}