package com.elearning.modal.dto.response;

import com.elearning.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Integer id;
    private Integer amount;
    private OrderStatus status;
    private LocalDateTime createdAt;


    private Integer userId;
    private String userFullName;
    private String userEmail;

    private Integer courseId;
    private String courseTitle;
}