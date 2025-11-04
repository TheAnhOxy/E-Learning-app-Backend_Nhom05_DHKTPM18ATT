package com.elearning.converter;


import com.elearning.entity.Category;
import com.elearning.entity.Course;
import com.elearning.entity.User;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.repository.CategoryRepository;
import com.elearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseConverter {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    public Course toEntity(CourseRequestDTO dto) {
        Course course = modelMapper.map(dto, Course.class);
        User instructor = userRepository.findById(dto.getInstructorId())
                .orElseThrow();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow();
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setId(null);

        return course;
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

        return dto;
    }
}
