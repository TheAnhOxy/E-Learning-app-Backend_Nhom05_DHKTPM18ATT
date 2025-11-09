package com.elearning.modal.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingOrderCheckResponse {
    private boolean hasPending;
    private Integer orderId;
    private Integer transactionId;
    private String qrCodeData;
}