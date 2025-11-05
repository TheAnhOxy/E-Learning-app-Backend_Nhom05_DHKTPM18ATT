package com.elearning.controller.student;


import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse> getReviewsByCourse(
            @PathVariable Integer courseId
    ) {
        log.info("Nhận yêu cầu lấy reviews cho khóa học ID: {}", courseId);
        // Gọi hàm service mới
        var reviewList = reviewService.getReviewsByCourseId(courseId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đánh giá thành công")
                .data(reviewList) // Trả về List
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-review/{courseId}")
    public ResponseEntity<ApiResponse> getMyReview(
            @PathVariable Integer courseId
    ) {
        log.info("Student (ID Gắn cứng) lấy review của mình cho khóa học ID: {}", courseId);

        var reviewData = reviewService.getMyReviewForCourse(courseId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy đánh giá của bạn thành công")
                .data(reviewData)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO
    ) {
        log.info("Student (ID Gắn cứng) tạo review cho khóa học ID: {}", reviewRequestDTO.getCourseId());

        var createdReview = reviewService.createReview(reviewRequestDTO);
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
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO
    ) {
        log.info("Student (ID Gắn cứng) cập nhật review ID: {}", reviewId);

        var updatedReview = reviewService.updateReview(reviewId, reviewRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật đánh giá thành công")
                .data(updatedReview)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteMyReview(
            @PathVariable Integer reviewId
    ) {
        log.info("Student (ID Gắn cứng) xóa review ID: {}", reviewId);

        reviewService.deleteReview(reviewId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa đánh giá thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}