package com.elearning.converter;

import com.elearning.entity.Course;
import com.elearning.modal.dto.response.MyCourseResponseDTO;
import com.elearning.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyCourseConverter {

    private final ModelMapper modelMapper;
    private final ProgressRepository progressRepository;

    public MyCourseResponseDTO toDTO(Course course, Integer userId) {
        MyCourseResponseDTO dto = modelMapper.map(course, MyCourseResponseDTO.class);

        int lessonCount = countLessons(course);
        int completedCount = countCompletedLessons(course, userId);
        double progressPercent = lessonCount > 0 ? (double) completedCount / lessonCount : 0.0;

        dto.setLessonCount(lessonCount);
        dto.setCompletedLessons(completedCount);
        dto.setProgressPercentage(Math.round(progressPercent * 100.0) / 100.0);

        return dto;
    }

    private int countLessons(Course course) {
        return course.getSections().stream()
                .mapToInt(section -> section.getLessons().size())
                .sum();
    }

    private int countCompletedLessons(Course course, Integer userId) {
        return progressRepository.countCompletedByCourseAndUser(course.getId(), userId);
    }
}
