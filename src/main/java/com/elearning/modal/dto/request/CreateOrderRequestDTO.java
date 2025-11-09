package com.elearning.modal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequestDTO {

    @NotNull(message = "ID Khóa học không được để trống")
    private Integer courseId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // VD: "Momo", "ZaloPay", "VnPay"
}