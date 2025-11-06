package com.elearning.controller.student;


import com.elearning.modal.dto.request.CreateOrderRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.CustomUserDetails;
import com.elearning.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('STUDENT')")
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
        log.info("Student ID {} yêu cầu tạo đơn hàng...", userId);

        var responseData = orderService.createOrder(dto, userId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo đơn hàng thành công, vui lòng quét QR.")
                .data(responseData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getMyOrder(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
        log.info("Student ID {} xem chi tiết đơn hàng ID: {}", userId, orderId);

        var orderData = orderService.getMyOrderDetails(orderId, userId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết đơn hàng thành công")
                .data(orderData)
                .build();
        return ResponseEntity.ok(response);
    }
}