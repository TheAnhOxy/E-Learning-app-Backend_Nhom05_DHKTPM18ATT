package com.elearning.modal.dto.response;

import com.elearning.enums.OrderStatus;
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
public class OrderDetailResponseDTO {
    private Integer id;
    private Integer amount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    // Thông tin User
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;

    private Integer courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    // Lịch sử giao dịch
    private List<TransactionResponseDTO> transactions;
}