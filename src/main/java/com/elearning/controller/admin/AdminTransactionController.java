package com.elearning.controller.admin;

import com.elearning.modal.dto.request.RefundRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.search.TransactionSearchRequest;
import com.elearning.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
@Slf4j
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse> searchTransactions(
            @Valid TransactionSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        log.info("Admin tìm kiếm giao dịch...");
        var transactionPage = transactionService.searchTransactions(searchRequest, pageable);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách giao dịch thành công")
                .data(transactionPage)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<ApiResponse> refundTransaction(
            @PathVariable Integer transactionId,
            @Valid @RequestBody RefundRequestDTO refundRequestDTO
    ) {
        log.info("Admin yêu cầu hoàn tiền cho giao dịch ID: {}", transactionId);

        var refundTransaction = transactionService.createRefund(transactionId, refundRequestDTO);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Hoàn tiền thành công!")
                .data(refundTransaction)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}