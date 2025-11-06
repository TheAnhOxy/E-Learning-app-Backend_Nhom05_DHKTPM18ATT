package com.elearning.modal.dto.response;

import com.elearning.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// DTO chi tiết đầy đủ của một đơn hàng
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDTO {
    private Integer id;
    private Integer amount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    // Thông tin User
    private Integer userId;
    private String userFullName;

    private Integer courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    // Lịch sử giao dịch
    private List<TransactionResponseDTO> transactions;
}