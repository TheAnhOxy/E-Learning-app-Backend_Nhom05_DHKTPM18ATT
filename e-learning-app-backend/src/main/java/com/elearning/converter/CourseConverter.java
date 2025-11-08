package com.elearning.converter;


import com.elearning.entity.Category;
import com.elearning.entity.Course;
import com.elearning.entity.User;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.repository.CategoryRepository;
import com.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseConverter {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public Course toEntity(CourseRequestDTO dto) {

        Course course = modelMapper.map(dto, Course.class);

        User instructor = userService.getUserEntityById(dto.getInstructorId());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow();

        course.setInstructor(instructor);
        course.setCategory(category);
        course.setId(null);
        return course;
    }

    public void updateEntity(Course existingCourse, CourseRequestDTO dto) {
        modelMapper.map(dto, existingCourse);
        if (!existingCourse.getInstructor().getId().equals(dto.getInstructorId())) {
            User instructor = userService.getUserEntityById(dto.getInstructorId());
            existingCourse.setInstructor(instructor);
        }

        if (!existingCourse.getCategory().getId().equals(dto.getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow();
            existingCourse.setCategory(category);
        }
    }



    public CourseResponseDTO toDTO(Course entity) {
        CourseResponseDTO dto = modelMapper.map(entity, CourseResponseDTO.class);
        if (entity.getInstructor() != null) {
            dto.setInstructorId(entity.getInstructor().getId());
            dto.setInstructorName(entity.getInstructor().getFullName());
            dto.setInstructorAvatar(entity.getInstructor().getAvatarUrl());
        }

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }

        dto.setRatingAvg(calculateAverageRating(entity));
        dto.setRatingCount(entity.getReviews() != null ? entity.getReviews().size() : 0);
        dto.setLessonCount(calculateLessonCount(entity));

        return dto;
    }

    public Double calculateAverageRating(Course course) {
        if (course.getReviews() == null || course.getReviews().isEmpty()) {
            return 5.0;
        }
        double sum = course.getReviews().stream()
                .mapToDouble(review -> review.getRating())
                .sum();
        return sum / course.getReviews().size();
    }

    public Integer calculateLessonCount(Course course) {
        if (course.getSections() == null || course.getSections().isEmpty()) {
            return 0;
        }
        return course.getSections().stream()
                .mapToInt(section -> section.getLessons() != null ? section.getLessons().size() : 0)
                .sum();
    }
}