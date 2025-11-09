package com.elearning.converter;

import com.elearning.entity.Lesson;
import com.elearning.entity.Section;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.LessonRequestDTO;
import com.elearning.modal.dto.response.LessonResponseDTO;
import com.elearning.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonConverter {

    private final ModelMapper modelMapper;
    private final SectionRepository sectionRepository;

    public Lesson toEntity(LessonRequestDTO dto) {
        Lesson lesson = modelMapper.map(dto, Lesson.class);
        Section section = sectionRepository.findById(dto.getSectionId())
                .orElseThrow();
        lesson.setSection(section);
        lesson.setId(null);
        return lesson;
    }


    public void updateEntity(Lesson existingLesson, LessonRequestDTO dto) {
        modelMapper.map(dto, existingLesson);
        if (!existingLesson.getSection().getId().equals(dto.getSectionId())) {
            Section section = sectionRepository.findById(dto.getSectionId())
                    .orElseThrow();
            existingLesson.setSection(section);
        }
    }


    public LessonResponseDTO toDTO(Lesson entity) {

        LessonResponseDTO dto = modelMapper.map(entity, LessonResponseDTO.class);

        if (entity.getSection() != null) {
            dto.setSectionId(entity.getSection().getId());
        }
        return dto;
    }
}