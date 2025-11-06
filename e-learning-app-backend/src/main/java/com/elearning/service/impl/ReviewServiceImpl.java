package com.elearning.service.impl;

import com.elearning.converter.ReviewConverter;
import com.elearning.entity.Course;
import com.elearning.entity.Review;
import com.elearning.entity.User;
import com.elearning.exception.ConflictException;
import com.elearning.exception.ForBiddenException;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.ReviewRepository;
import com.elearning.service.ReviewService;
import com.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {


    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final ReviewConverter reviewConverter;

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto, Integer userId) {
        log.info("Student ID {} đang tạo review cho khóa học ID {}", userId, dto.getCourseId());

        if (reviewRepository.findByUserIdAndCourseId(userId, dto.getCourseId()).isPresent()) {
            throw new ConflictException("Bạn đã đánh giá khóa học này rồi.");
        }

        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, dto.getCourseId())) {
            // Sửa lại tên Exception nếu bạn dùng ForbiddenException
            throw new ForBiddenException("Bạn phải tham gia khóa học trước khi đánh giá.");
        }

        User user = userService.getUserEntityById(userId);
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow();

        Review review = reviewConverter.toEntity(dto, user, course);
        Review savedReview = reviewRepository.save(review);
        log.info("Đã tạo review thành công ID: {}", savedReview.getId());

        return reviewConverter.toDTO(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Integer reviewId, ReviewRequestDTO dto, Integer userId) { // <-- SỬA LẠI
        log.info("Student ID {} đang cập nhật review ID {}", userId, reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();

        if (!review.getUser().getId().equals(userId)) {
            throw new ForBiddenException("Bạn không có quyền sửa đánh giá này.");
        }
        if (!review.getCourse().getId().equals(dto.getCourseId())) {
            throw new ConflictException("Không thể thay đổi khóa học của đánh giá.");
        }
        reviewConverter.updateEntity(review, dto);
        Review updatedReview = reviewRepository.save(review);

        return reviewConverter.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        log.info("Student ID {} đang xóa review ID {}", userId, reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();

        if (!review.getUser().getId().equals(userId)) {
            throw new ForBiddenException("Bạn không có quyền xóa đánh giá này.");
        }
        reviewRepository.delete(review);
        log.info("Student đã xóa review thành công.");
    }

    @Override
    @Transactional
    public void adminDeleteReview(Integer reviewId) {
        log.info("Admin đang xóa review ID {}", reviewId);
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
        log.info("Admin đã xóa review thành công.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByCourseId(Integer courseId) {
        log.info("Lấy danh sách (không phân trang) review cho khóa học ID {}", courseId);
        List<Review> reviews = reviewRepository.findAllByCourseIdOrderByCreatedAtDesc(courseId);
        return reviews.stream()
                .map(reviewConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getMyReviewForCourse(Integer courseId, Integer userId) {
        log.info("Student ID {} đang kiểm tra review cho khóa học ID {}", userId, courseId);
        Review review = reviewRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return reviewConverter.toDTO(review);
    }

    // ---------------- Additional Methods ----------------//

    @Override
    public Page<ReviewResponseDTO> getAllReviewByCourseId(Integer courseId, int page, int limit) {
        log.info("Lấy tất cả review cho khóa học ID {} - trang {}, giới hạn {}", courseId, page, limit);
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByCourse_IdOrderByCreatedAtDesc(courseId, pageable);

        List<ReviewResponseDTO> reviewDTOs = reviewPage.getContent()
                .stream()
                .map(reviewConverter::toDTO)
                .toList();

        Page<ReviewResponseDTO> dtoPage = new PageImpl<>(reviewDTOs, pageable, reviewPage.getTotalElements());
        log.info("Đã lấy được {} review cho khóa học ID {}", reviewDTOs.size(), courseId);
        return dtoPage;
    }

}