package com.elearning.modal.dto.search;

import com.elearning.enums.TransactionStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TransactionSearchRequest {

    private String query;
    private Integer orderId;
    private Integer userId;
    private String transactionCode;
    private String paymentMethod;
    private TransactionStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}