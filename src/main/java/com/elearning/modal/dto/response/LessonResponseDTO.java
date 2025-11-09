package com.elearning.modal.dto.response;

import com.elearning.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponseDTO {
    private Integer id;
    private String title;
    private String provider;
    private String playbackUrl;
    private String videoDescription;
    private Integer durationInSeconds;
    private Integer sectionId;
    private Long viewsCount;
    private Boolean isFree;
    private Integer orderIndex;
    private List<QuizResponseDTO> quizzes;
}