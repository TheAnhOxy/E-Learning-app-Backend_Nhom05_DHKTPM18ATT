package com.elearning.service;

import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewResponseDTO createReview(ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(Integer reviewId, ReviewRequestDTO dto);
    void deleteReview(Integer reviewId);
    void adminDeleteReview(Integer reviewId);
    List<ReviewResponseDTO> getReviewsByCourseId(Integer courseId);
    ReviewResponseDTO getMyReviewForCourse(Integer courseId);
}