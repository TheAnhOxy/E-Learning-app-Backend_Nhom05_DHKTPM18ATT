package com.elearning.modal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequestDTO {

    @NotBlank(message = "Tiêu đề chương không được để trống")
    private String title;

    @NotNull(message = "ID Khóa học không được để trống")
    private Integer courseId;

    private Integer orderIndex;
}