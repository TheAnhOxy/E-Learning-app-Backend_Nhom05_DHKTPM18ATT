package com.elearning.modal.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private Integer certificateId;
    private Integer courseId;
    private String courseTitle;
    private String certificateCode;
    private LocalDateTime issueDate;
}