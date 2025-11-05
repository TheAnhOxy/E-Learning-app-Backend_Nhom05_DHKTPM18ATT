package com.elearning.converter;

import com.elearning.entity.Course;
import com.elearning.entity.Section;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.SectionRequestDTO;
import com.elearning.modal.dto.response.SectionResponseDTO;
import com.elearning.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionConverter {

    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;

    public Section toEntity(SectionRequestDTO dto) {
        Section section = modelMapper.map(dto, Section.class);
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow();
        section.setCourse(course);
        section.setId(null);
        return section;
    }

    public void updateEntity(Section existingSection, SectionRequestDTO dto) {
        modelMapper.map(dto, existingSection);
        if (!existingSection.getCourse().getId().equals(dto.getCourseId())) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow();
            existingSection.setCourse(course);
        }
    }

    public SectionResponseDTO toDTO(Section entity) {
        SectionResponseDTO dto = modelMapper.map(entity, SectionResponseDTO.class);

        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        // dto.setLessons(...);
        return dto;
    }
}