package com.elearning.modal.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequestDTO {

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    @NotNull(message = "ID Chương (Section) không được để trống")
    private Integer sectionId;

    private String provider;
    private String providerVideoId;
    private String playbackUrl;
    private String videoDescription;
    private Integer durationInSeconds;
    private String thumbnailUrl;
    private Boolean isFree;
    private Integer orderIndex;
}