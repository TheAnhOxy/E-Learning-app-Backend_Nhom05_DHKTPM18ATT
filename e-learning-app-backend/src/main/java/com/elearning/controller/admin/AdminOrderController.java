package com.elearning.controller.admin;

import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.search.OrderSearchRequest;
import com.elearning.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;


    @GetMapping
    public ResponseEntity<ApiResponse> searchOrders(
            @Valid OrderSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        log.info("Admin tìm kiếm đơn hàng...");
        var orderPage = orderService.searchOrders(searchRequest, pageable);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đơn hàng thành công")
                .data(orderPage)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderDetails(
            @PathVariable Integer orderId
    ) {
        log.info("Admin xem chi tiết đơn hàng ID: {}", orderId);

        var orderData = orderService.getOrderDetailsById(orderId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết đơn hàng thành công")
                .data(orderData)
                .build();
        return ResponseEntity.ok(response);
    }
}