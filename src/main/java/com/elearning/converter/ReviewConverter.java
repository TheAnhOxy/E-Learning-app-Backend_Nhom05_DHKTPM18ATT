package com.elearning.converter;


import com.elearning.entity.Course;
import com.elearning.entity.Review;
import com.elearning.entity.User;
import com.elearning.modal.dto.request.ReviewRequestDTO;
import com.elearning.modal.dto.response.ReviewResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewConverter {

    private final ModelMapper modelMapper;

    public ReviewResponseDTO toDTO(Review entity) {
        ReviewResponseDTO dto = modelMapper.map(entity, ReviewResponseDTO.class);
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
            dto.setUserAvatarUrl(entity.getUser().getAvatarUrl());
        }
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        return dto;
    }

    public Review toEntity(ReviewRequestDTO dto, User user, Course course) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setCourse(course);
        return review;
    }


    public void updateEntity(Review existingReview, ReviewRequestDTO dto) {
        existingReview.setRating(dto.getRating());
        existingReview.setComment(dto.getComment());
    }
}