package com.elearning.modal.dto.request;

import com.elearning.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    private String thumbnailUrl;

    @NotNull(message = "Giá không được để trống")
    @PositiveOrZero(message = "Giá phải lớn hơn hoặc bằng 0")
    private Integer price;

    @PositiveOrZero(message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private Integer originalPrice;

    @NotNull(message = "ID Giảng viên không được để trống")
    private Integer instructorId;

    @NotNull(message = "ID Danh mục không được để trống")
    private Integer categoryId;

    @NotNull
    private CourseStatus status;

    private String videoPreviewUrl;
}