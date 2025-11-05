package com.elearning.modal.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundRequestDTO {
    @NotBlank(message = "Lý do hoàn tiền không được để trống")
    private String reason;
}