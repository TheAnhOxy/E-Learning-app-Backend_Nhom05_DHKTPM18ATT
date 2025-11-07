package com.elearning.service;

import com.elearning.modal.dto.request.CreateOrderRequestDTO;
import com.elearning.modal.dto.response.CreateOrderResponseDTO;
import com.elearning.modal.dto.response.OrderDetailResponseDTO;
import com.elearning.modal.dto.response.OrderResponseDTO;
import com.elearning.modal.dto.search.OrderSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO dto, Integer userId);
    OrderDetailResponseDTO getMyOrderDetails(Integer orderId, Integer userId);
    OrderDetailResponseDTO getOrderDetailsById(Integer orderId);
    Page<OrderResponseDTO> searchOrders(OrderSearchRequest searchRequest, Pageable pageable);
    void markOrderAsCompleted(Integer orderId);

    void deleteOrder(Integer orderId);

}