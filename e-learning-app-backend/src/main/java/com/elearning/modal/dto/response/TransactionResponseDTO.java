package com.elearning.modal.dto.response;

import com.elearning.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Integer id;
    private Integer orderId;
    private Integer amount;
    private TransactionStatus status;
    private String paymentMethod;
    private String transactionCode;
    private LocalDateTime createdAt;

    private String userFullName;
    private String courseTitle;
}