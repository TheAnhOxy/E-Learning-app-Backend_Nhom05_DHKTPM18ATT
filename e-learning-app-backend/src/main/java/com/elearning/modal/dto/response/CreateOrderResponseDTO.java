package com.elearning.modal.dto.response;

import com.elearning.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO trả về khi Student tạo đơn hàng, chứa thông tin QR Code
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponseDTO {

    private Integer orderId;
    private Integer transactionId; //'pending' vừa tạo
    private Integer amount;
    private OrderStatus orderStatus;

    // Front-end render QR
    private String qrCodeData; //  chứa URL (vd: momo://...)
    private String qrCodeImageUrl; // ) Link ảnh QR ( gen QR ở server)
}