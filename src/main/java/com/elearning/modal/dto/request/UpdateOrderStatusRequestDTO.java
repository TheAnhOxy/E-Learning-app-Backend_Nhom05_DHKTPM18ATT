package com.elearning.modal.dto.request;

import com.elearning.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private OrderStatus status;
}