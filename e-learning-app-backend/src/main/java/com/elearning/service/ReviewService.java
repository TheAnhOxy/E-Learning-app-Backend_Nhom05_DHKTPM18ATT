package com.elearning.service;

import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponseDTO createReview(ReviewRequestDTO dto, Integer userId);

    ReviewResponseDTO updateReview(Integer reviewId, ReviewRequestDTO dto, Integer userId);
    void deleteReview(Integer reviewId, Integer userId);
    void adminDeleteReview(Integer reviewId);
    Page<ReviewResponseDTO> getReviewsByCourseId(Integer courseId, Pageable pageable);
    ReviewResponseDTO getMyReviewForCourse(Integer courseId, Integer userId);

    Page<ReviewResponseDTO> getAllReviewByCourseId(Integer courseId, int page, int limit);
}