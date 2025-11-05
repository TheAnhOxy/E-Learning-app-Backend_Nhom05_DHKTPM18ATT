package com.elearning.service;

import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO dto, Integer userId); // <-- SỬA LẠI
    ReviewResponseDTO updateReview(Integer reviewId, ReviewRequestDTO dto, Integer userId);
    void deleteReview(Integer reviewId, Integer userId);
    void adminDeleteReview(Integer reviewId);
    List<ReviewResponseDTO> getReviewsByCourseId(Integer courseId);

    ReviewResponseDTO getMyReviewForCourse(Integer courseId, Integer userId);
}