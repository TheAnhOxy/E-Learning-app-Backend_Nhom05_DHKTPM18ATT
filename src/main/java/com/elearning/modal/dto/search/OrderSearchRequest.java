package com.elearning.modal.dto.search;

import com.elearning.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderSearchRequest {
    private String query;
    private OrderStatus status;
    private Integer userId;
    private Integer courseId;
    // ...
}