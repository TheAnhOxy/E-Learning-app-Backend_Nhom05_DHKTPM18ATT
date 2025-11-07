package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseBriefResponseDTO {
    private Integer id;
    private String title;
    private String thumbnailUrl;
    private String status;
}